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
    private char mMap[][];

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

        public DrawingThread(SurfaceHolder surfaceHolder) {
            this.mSurfaceHolder = surfaceHolder;
        }

        public void setRunning(boolean running) {
            this.mRunning = running;
        }

        @Override
        public void run() {
            Canvas canvas;
            GlobalConfigurations globalConfigurations = GlobalConfigurations.getInstance(mContext);
            mMap = globalConfigurations.getMap();
            final int totalHeight = DrawingView.this.getHeight();
            final int totalWidth = DrawingView.this.getWidth();
            //Log.d(TAG, "screen: " + totalHeight + "x" + totalWidth);
            final int heightInBlocks = mMap.length;
            final int widthInBlocks = mMap[0].length;
            final int blockHeight = (int) Math.floor(((double) totalHeight) / heightInBlocks);
            final int blockWidth = (int) Math.floor(((double) totalWidth) / widthInBlocks);
            Paint redPaint = new Paint();
            Paint whitePaint = new Paint();
            redPaint.setStrokeWidth(1);
            whitePaint.setStrokeWidth(1);
            redPaint.setColor(Color.RED);
            whitePaint.setColor(Color.WHITE);

            DrawingView.this.mContext.registerReceiver(new BotMessagesBroadcastReceiver(),
                    new IntentFilter(Robot.ROBOT_MESSAGES_FILTER));

            Bitmap boxBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.box_light);
            boxBitmap = Bitmap.createScaledBitmap(boxBitmap, blockWidth, blockHeight, false);
            Bitmap robotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.robot);
            robotBitmap = Bitmap.createScaledBitmap(robotBitmap, blockWidth, blockHeight, false);

            Robot robot = new Robot(mContext, mMap, 1);
            while (mRunning) {
                canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;
                    canvas.drawColor(Color.GREEN);
                    robot.update();
                    for (int i = 0; i < mMap.length; i++) {
                        for (int j = 0; j < mMap[i].length; j++) {
                            Paint paint = null;
                            if (mMap[i][j] == '-') {
                                paint = redPaint;
                            } else {
                                paint = whitePaint;
                            }
                            if (mMap[i][j] == GlobalConfigurations.SYMBOL_BOX) {
                                canvas.drawBitmap(boxBitmap, j * blockWidth, i * blockHeight, null);
                            } else if (mMap[i][j] == GlobalConfigurations.SYMBOL_ROBOT) {
                                Log.d(TAG, "run: Drawing Robot");
                                canvas.drawBitmap(robotBitmap, j * blockWidth, i * blockHeight, null);
                            } else {
                                canvas.drawRect(j * blockWidth, i * blockHeight,
                                        j * blockWidth + blockWidth, i * blockHeight + blockHeight, paint);
                            }
                        }
                    }
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
    }

    class BotMessagesBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int x = intent.getExtras().getInt("x");
            int y = intent.getExtras().getInt("y");
            Log.d(TAG, "onReceive: x= " + x + " y= " + y);
            mMap[y][x] = GlobalConfigurations.SYMBOL_ROBOT;
        }
    }
}
