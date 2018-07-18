package com.kimentii.virtualstorage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawingView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = DrawingView.class.getSimpleName();

    private DrawingThread mDrawingThread;
    private Context mContext;

    public DrawingView(Context context) {
        super(context);
        mContext = context;
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mDrawingThread = new DrawingThread(getHolder());
        mDrawingThread.setRunning(true);
        mDrawingThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        mDrawingThread.setRunning(false);
        while (retry) {
            try {
                mDrawingThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    class DrawingThread extends Thread {
        private boolean mRunning = false;
        private SurfaceHolder mSurfaceHolder;
        private char mMap[][];


        private Paint mRedPaint;
        private Paint mWhitPaint;
        private int mBlockHeight;
        private int mBlockWidth;
        private Bitmap mBoxBitmap;
        private Bitmap mRobotBitmap;

        public DrawingThread(SurfaceHolder surfaceHolder) {
            this.mSurfaceHolder = surfaceHolder;
            GlobalConfigurations globalConfigurations = GlobalConfigurations.getInstance(mContext);
            mMap = globalConfigurations.getMap();
            final int totalHeight = DrawingView.this.getHeight();
            final int totalWidth = DrawingView.this.getWidth();
            //Log.d(TAG, "screen: " + totalHeight + "x" + totalWidth);
            final int heightInBlocks = mMap.length;
            final int widthInBlocks = mMap[0].length;
            mBlockHeight = (int) Math.floor(((double) totalHeight) / heightInBlocks);
            mBlockWidth = (int) Math.floor(((double) totalWidth) / widthInBlocks);
            mRedPaint = new Paint();
            mWhitPaint = new Paint();
            mRedPaint.setStrokeWidth(1);
            mWhitPaint.setStrokeWidth(1);
            mRedPaint.setColor(Color.RED);
            mWhitPaint.setColor(Color.WHITE);

            DrawingView.this.mContext.registerReceiver(new BotMessagesBroadcastReceiver(mMap),
                    new IntentFilter(Robot.ROBOT_MESSAGES_FILTER));

            mBoxBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.box_light);
            mBoxBitmap = Bitmap.createScaledBitmap(mBoxBitmap, mBlockWidth, mBlockHeight, false);
            mRobotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.robot);
            mRobotBitmap = Bitmap.createScaledBitmap(mRobotBitmap, mBlockWidth, mBlockHeight, false);
        }

        public void setRunning(boolean running) {
            this.mRunning = running;
        }

        @Override
        public void run() {
            Canvas canvas;


            Robot robot = new Robot(mContext, mMap, 1);
            while (mRunning) {
                canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;

                    robot.update();
                    drawMap(canvas);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private void drawMap(Canvas canvas) {
            canvas.drawColor(Color.GREEN);
            for (int i = 0; i < mMap.length; i++) {
                for (int j = 0; j < mMap[i].length; j++) {
                    if (mMap[i][j] == GlobalConfigurations.SYMBOL_BOX) {
                        canvas.drawBitmap(mBoxBitmap, j * mBlockWidth, i * mBlockHeight, null);
                    } else if (mMap[i][j] == GlobalConfigurations.SYMBOL_ROBOT) {
                        canvas.drawBitmap(mRobotBitmap, j * mBlockWidth, i * mBlockHeight, null);
                    } else {
                        Paint paint = null;
                        if (mMap[i][j] == GlobalConfigurations.SYMBOL_BARRIER) {
                            paint = mRedPaint;
                        } else {
                            paint = mWhitPaint;
                        }
                        canvas.drawRect(j * mBlockWidth, i * mBlockHeight,
                                j * mBlockWidth + mBlockWidth, i * mBlockHeight + mBlockHeight, paint);
                    }
                }
            }
        }

    }

    class BotMessagesBroadcastReceiver extends BroadcastReceiver {
        private char[][] mMap;

        BotMessagesBroadcastReceiver(char[][] map) {
            mMap = map;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int x = intent.getExtras().getInt("x");
            int y = intent.getExtras().getInt("y");
            Log.d(TAG, "onReceive: x= " + x + " y= " + y);
            mMap[y][x] = GlobalConfigurations.SYMBOL_ROBOT;
        }
    }
}
