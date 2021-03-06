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
import android.os.Handler;
import android.os.Message;
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
    private Handler mLogHandler;
    private Map mMap;
    private RobotsCommandsReceiver mRobotsCommandsReceiver;

    private Paint mRedPaint;
    private Paint mWhitePaint;
    private int mBlockHeight;
    private int mBlockWidth;
    private Bitmap mBoxBitmap;
    private Bitmap mRobotBitmap;
    private Bitmap mReservedBoxBitmap;
    private Bitmap mStartBitmap;
    private Bitmap mEndBitmap;
    private ArrayList<Robot> mRobots;

    public DrawingView(Context context, Handler logHandler, int robotsNum) {
        super(context);
        //Log.d(TAG, "DrawingView");
        mContext = context;
        mLogHandler = logHandler;
        getHolder().addCallback(this);
        mMap = GlobalConfigurations.getInstance(mContext).getMapCopy();

        mRedPaint = new Paint();
        mWhitePaint = new Paint();
        mRedPaint.setStrokeWidth(1);
        mWhitePaint.setStrokeWidth(1);
        mRedPaint.setColor(Color.RED);
        mWhitePaint.setColor(Color.WHITE);

        setRobotsNum(robotsNum);

        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Message message = new Message();
        message.what = MainActivity.ACTION_CLEAR_LOG;
        mLogHandler.sendMessage(message);

        mMap = GlobalConfigurations.getInstance(mContext).getMapCopy();
        //Log.d(TAG, "onDraw: " + mMap);
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
        final int heightInBlocks = mMap.getMapHeight();
        final int widthInBlocks = mMap.getMapWidth();
        mBlockHeight = (int) Math.floor(((double) totalHeight) / heightInBlocks);
        mBlockWidth = (int) Math.floor(((double) totalWidth) / widthInBlocks);

        mBoxBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.box_light);
        mBoxBitmap = Bitmap.createScaledBitmap(mBoxBitmap, mBlockWidth, mBlockHeight, false);
        mRobotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.robot);
        mRobotBitmap = Bitmap.createScaledBitmap(mRobotBitmap, mBlockWidth, mBlockHeight, false);
        mReservedBoxBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.box_light);
        mReservedBoxBitmap = Bitmap.createScaledBitmap(mReservedBoxBitmap, mBlockWidth, mBlockHeight, false);
        mStartBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.start_image);
        mStartBitmap = Bitmap.createScaledBitmap(mStartBitmap, mBlockWidth, mBlockHeight, false);
        mEndBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.end_image);
        mEndBitmap = Bitmap.createScaledBitmap(mEndBitmap, mBlockWidth, mBlockHeight, false);

        mRobotsCommandsReceiver = new RobotsCommandsReceiver();
        LocalBroadcastManager.getInstance(DrawingView.this.getContext()).registerReceiver(
                mRobotsCommandsReceiver,
                new IntentFilter(Robot.ROBOTS_COMMANDS_FILTER));
//        Log.d(TAG, "surfaceCreated: ");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//        Log.d(TAG, "surfaceChanged: ");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//        Log.d(TAG, "surfaceDestroyed: ");
        for (int i = 0; i < mRobots.size(); i++) {
            mRobots.get(i).die();
        }
        LocalBroadcastManager.getInstance(DrawingView.this.getContext())
                .unregisterReceiver(mRobotsCommandsReceiver);
    }

    public void setRobotsNum(int robotsNum) {
        if (mRobots != null) {
            for (int i = 0; i < mRobots.size(); i++) {
                mRobots.get(i).die();
            }
        }
        mRobots = new ArrayList<>();
        for (int i = 0; i < robotsNum; i++) {
            mRobots.add(new Robot(mContext, GlobalConfigurations.getInstance(mContext).getMapCopy(),
                    i + 1, CommandsFactory.getAllCommands()));
        }
    }

    public void startDrawing() {
        setWillNotDraw(true);
        mDrawingThread = new DrawingThread(getHolder());
        mDrawingThread.setRunning(true);
        mDrawingThread.start();
    }

    public void stopDrawing() {
        boolean retry = true;
        mDrawingThread.setRunning(false);
        while (retry) {
            try {
                mDrawingThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
        setRobotsNum(mRobots.size());
        setWillNotDraw(false);
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
                        paint = mWhitePaint;
                    }
                    canvas.drawRect(x * mBlockWidth, y * mBlockHeight,
                            x * mBlockWidth + mBlockWidth, y * mBlockHeight + mBlockHeight, paint);
                }
            }
        }
    }

    public void destroy() {
        for (int i = 0; i < mRobots.size(); i++) {
            mRobots.get(i).die();
        }
        LocalBroadcastManager.getInstance(DrawingView.this.getContext())
                .unregisterReceiver(mRobotsCommandsReceiver);
    }

    class DrawingThread extends Thread {
        private boolean mRunning = false;
        private SurfaceHolder mSurfaceHolder;


        DrawingThread(SurfaceHolder surfaceHolder) {
            this.mSurfaceHolder = surfaceHolder;
        }

        void setRunning(boolean running) {
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
                    //Log.d(TAG, "run: Drawing: " + mMap);
                    for (int i = 0; i < mRobots.size(); i++) {
                        mRobots.get(i).update();
                    }
                    drawMap(canvas);
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
            Command command = (Command) intent.getExtras().getSerializable(EXTRA_COMMAND);
            command.updateMap(mMap);

            Message message = new Message();
            message.what = MainActivity.ACTION_LOG;
            message.obj = command.toString();
            mLogHandler.sendMessage(message);
        }
    }
}
