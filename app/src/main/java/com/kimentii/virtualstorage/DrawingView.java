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
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kimentii.virtualstorage.commands.Command;

import java.util.ArrayList;

import static com.kimentii.virtualstorage.Robot.EXTRA_COMMAND;

public class DrawingView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = DrawingView.class.getSimpleName();

    private DrawingThread mDrawingThread;
    private Context mContext;
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
    private ArrayList<Robot> mRobots;

    public DrawingView(Context context, int robotsNum) {
        super(context);
        mContext = context;
        getHolder().addCallback(this);
        mMap = GlobalConfigurations.getInstance(mContext).getMapCopy();

        mRedPaint = new Paint();
        mWhitPaint = new Paint();
        mRedPaint.setStrokeWidth(1);
        mWhitPaint.setStrokeWidth(1);
        mRedPaint.setColor(Color.RED);
        mWhitPaint.setColor(Color.WHITE);

        setRobotsNum(robotsNum);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mMap = GlobalConfigurations.getInstance(mContext).getMapCopy();
        Log.d(TAG, "onDraw: ");
        if (mRobots != null) {
            for (int i = 0; i < mRobots.size(); i++) {
                mRobots.get(i).die();
            }
        }
        for (int i = 0; i < mRobots.size(); i++) {
            mRobots.get(i).setMap(mMap);
            mRobots.get(i).born();
        }
        drawMap(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        final int totalHeight = getHeight();
        final int totalWidth = getWidth();
        //Log.d(TAG, "screen: " + totalHeight + "x" + totalWidth);
        final int heightInBlocks = mMap.getMapHeight();
        final int widthInBlocks = mMap.getMapWidth();
        mBlockHeight = (int) Math.floor(((double) totalHeight) / heightInBlocks);
        mBlockWidth = (int) Math.floor(((double) totalWidth) / widthInBlocks);

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

        mRobotsCommandsReceiver = new RobotsCommandsReceiver();
        LocalBroadcastManager.getInstance(DrawingView.this.getContext()).registerReceiver(
                mRobotsCommandsReceiver,
                new IntentFilter(Robot.ROBOTS_COMMANDS_FILTER));
        Log.d(TAG, "surfaceCreated: ");

        /*mDrawingThread = new DrawingThread(getHolder());
        mDrawingThread.setRunning(true);
        mDrawingThread.start();*/
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d(TAG, "surfaceChanged: ");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroyed: ");
       /* boolean retry = true;
        mDrawingThread.setRunning(false);
        while (retry) {
            try {
                mDrawingThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }*/
        for (int i = 0; i < mRobots.size(); i++) {
            mRobots.get(i).die();
        }
        LocalBroadcastManager.getInstance(DrawingView.this.getContext())
                .unregisterReceiver(mRobotsCommandsReceiver);
    }

    public void setRobotsNum(int robotsNum) {
        mRobots = new ArrayList<>();
        for (int i = 0; i < robotsNum; i++) {
            mRobots.add(new Robot(mContext, GlobalConfigurations.getInstance(mContext).getMapCopy(),
                    i + 1, CommandsFactory.getAllCommands()));
        }
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

            while (mRunning) {
                canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;

                    drawMap(canvas);
                    for (int i = 0; i < mRobots.size(); i++) {
                        mRobots.get(i).update();
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
        }
    }

    public class RobotsCommandsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            Command command = (Command) intent.getExtras().getSerializable(EXTRA_COMMAND);
            command.updateMap(mMap);
        }
    }
}
