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
import android.support.v4.content.LocalBroadcastManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kimentii.virtualstorage.commands.Command;

import java.util.ArrayList;

import static com.kimentii.virtualstorage.Robot.EXTRA_COMMAND;

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
        private Map mMap;
        private RobotsCommandsReceiver mRobotsCommandsReceiver;


        private Paint mRedPaint;
        private Paint mWhitPaint;
        private int mBlockHeight;
        private int mBlockWidth;
        private Bitmap mBoxBitmap;
        private Bitmap mRobotBitmap;
        private Bitmap mReservedBoxBitmap;
        private Bitmap mStartBitmap;
        private Bitmap mEndBitmap;

        public DrawingThread(SurfaceHolder surfaceHolder) {
            this.mSurfaceHolder = surfaceHolder;
            GlobalConfigurations globalConfigurations = GlobalConfigurations.getInstance(mContext);
            mMap = globalConfigurations.getMapCopy();
            final int totalHeight = DrawingView.this.getHeight();
            final int totalWidth = DrawingView.this.getWidth();
            //Log.d(TAG, "screen: " + totalHeight + "x" + totalWidth);
            final int heightInBlocks = mMap.getMapHeight();
            final int widthInBlocks = mMap.getMapWidth();
            mBlockHeight = (int) Math.floor(((double) totalHeight) / heightInBlocks);
            mBlockWidth = (int) Math.floor(((double) totalWidth) / widthInBlocks);
            mRedPaint = new Paint();
            mWhitPaint = new Paint();
            mRedPaint.setStrokeWidth(1);
            mWhitPaint.setStrokeWidth(1);
            mRedPaint.setColor(Color.RED);
            mWhitPaint.setColor(Color.WHITE);

            mBoxBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.box_light);
            mBoxBitmap = Bitmap.createScaledBitmap(mBoxBitmap, mBlockWidth, mBlockHeight, false);
            mRobotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.robot);
            mRobotBitmap = Bitmap.createScaledBitmap(mRobotBitmap, mBlockWidth, mBlockHeight, false);
            mReservedBoxBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.box);
            mReservedBoxBitmap = Bitmap.createScaledBitmap(mReservedBoxBitmap, mBlockWidth, mBlockHeight, false);
            mStartBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.start_image);
            mStartBitmap = Bitmap.createScaledBitmap(mStartBitmap, mBlockWidth, mBlockHeight, false);
            mEndBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.end_image);
            mEndBitmap = Bitmap.createScaledBitmap(mEndBitmap, mBlockWidth, mBlockHeight, false);
        }

        public void setRunning(boolean running) {
            this.mRunning = running;
        }

        @Override
        public void run() {
            Canvas canvas;

            mRobotsCommandsReceiver = new RobotsCommandsReceiver(mMap);
            LocalBroadcastManager.getInstance(DrawingView.this.getContext()).registerReceiver(
                    mRobotsCommandsReceiver,
                    new IntentFilter(Robot.ROBOTS_COMMANDS_FILTER));

            ArrayList<Robot> robots = new ArrayList<>();
            robots.add(new Robot(mContext, mMap, 1, CommandsFactory.getAllCommands()));

            while (mRunning) {
                canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;

                    drawMap(canvas);
                    for (int i = 0; i < robots.size(); i++) {
                        robots.get(i).update();
                    }

                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < robots.size(); i++) {
                robots.get(i).destroy();
            }
            LocalBroadcastManager.getInstance(DrawingView.this.getContext())
                    .unregisterReceiver(mRobotsCommandsReceiver);
        }

        private void drawMap(Canvas canvas) {
            canvas.drawColor(Color.GREEN);
            for (int y = 0; y < mMap.getMapHeight(); y++) {
                for (int x = 0; x < mMap.getMapWidth(); x++) {
                    if (mMap.getSymbolAt(x, y) == GlobalConfigurations.SYMBOL_FREE_BOX) {
                        canvas.drawBitmap(mBoxBitmap, x * mBlockWidth, y * mBlockHeight, null);
                    } else if (mMap.getSymbolAt(x, y) == GlobalConfigurations.SYMBOL_RESERVED_BOX) {
                        canvas.drawBitmap(mReservedBoxBitmap, x * mBlockWidth, y * mBlockHeight, null);
                    } else if (mMap.getSymbolAt(x, y) == GlobalConfigurations.SYMBOL_ROBOT) {
                        canvas.drawBitmap(mRobotBitmap, x * mBlockWidth, y * mBlockHeight, null);
                    } else if (mMap.getSymbolAt(x, y) == GlobalConfigurations.SYMBOL_START) {
                        canvas.drawBitmap(mStartBitmap, x * mBlockWidth, y * mBlockHeight, null);
                    } else if (mMap.getSymbolAt(x, y) == GlobalConfigurations.SYMBOL_END) {
                        canvas.drawBitmap(mEndBitmap, x * mBlockWidth, y * mBlockHeight, null);
                    } else {
                        Paint paint = null;
                        if (mMap.getSymbolAt(x, y) == GlobalConfigurations.SYMBOL_BARRIER) {
                            paint = mRedPaint;
                        } else {
                            paint = mWhitPaint;
                        }
                        canvas.drawRect(x * mBlockWidth, y * mBlockHeight,
                                x * mBlockWidth + mBlockWidth, y * mBlockHeight + mBlockHeight, paint);
                    }
                }
            }
        }
    }

    public static class RobotsCommandsReceiver extends BroadcastReceiver {
        private Map mMap;

        public RobotsCommandsReceiver(Map map) {
            mMap = map;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Command command = (Command) intent.getExtras().getSerializable(EXTRA_COMMAND);
            command.updateMap(mMap);
        }
    }
}
