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
import com.kimentii.virtualstorage.commands.MoveCommand;
import com.kimentii.virtualstorage.commands.ReserveBoxCommand;

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


        private Paint mRedPaint;
        private Paint mWhitPaint;
        private int mBlockHeight;
        private int mBlockWidth;
        private Bitmap mBoxBitmap;
        private Bitmap mRobotBitmap;
        private Bitmap mReservedBoxBitmap;

        public DrawingThread(SurfaceHolder surfaceHolder) {
            this.mSurfaceHolder = surfaceHolder;
            GlobalConfigurations globalConfigurations = GlobalConfigurations.getInstance(mContext);
            mMap = globalConfigurations.getMap();
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

            LocalBroadcastManager.getInstance(DrawingView.this.getContext()).registerReceiver(
                    new BotMessagesBroadcastReceiver(mMap),
                    new IntentFilter(Robot.ROBOTS_COMMANDS_FILTER));

            mBoxBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.box_light);
            mBoxBitmap = Bitmap.createScaledBitmap(mBoxBitmap, mBlockWidth, mBlockHeight, false);
            mRobotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.robot);
            mRobotBitmap = Bitmap.createScaledBitmap(mRobotBitmap, mBlockWidth, mBlockHeight, false);
            mReservedBoxBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.box);
            mReservedBoxBitmap = Bitmap.createScaledBitmap(mReservedBoxBitmap, mBlockWidth, mBlockHeight, false);
        }

        public void setRunning(boolean running) {
            this.mRunning = running;
        }

        @Override
        public void run() {
            Canvas canvas;

            ArrayList<Command> commands = new ArrayList<>();
            commands.add(new MoveCommand());
            commands.add(new ReserveBoxCommand());
            Robot robot = new Robot(mContext, mMap, 1, commands);

            ArrayList<Command> commands1 = new ArrayList<>();
            commands1.add(new MoveCommand());
            commands1.add(new ReserveBoxCommand());
            Robot robot1 = new Robot(mContext, mMap, 1, commands1);
            while (mRunning) {
                canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;

                    drawMap(canvas);
                    robot.update();
                    robot1.update();

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

    class BotMessagesBroadcastReceiver extends BroadcastReceiver {
        private Map mMap;

        BotMessagesBroadcastReceiver(Map map) {
            mMap = map;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Command command = (Command) intent.getExtras().getSerializable(EXTRA_COMMAND);
            command.updateMap(mMap);
        }
    }
}
