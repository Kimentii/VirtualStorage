package com.kimentii.virtualstorage;

import android.content.Context;
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
            char map[][] = globalConfigurations.getMap();
            final int totalHeight = DrawingView.this.getHeight();
            final int totalWidth = DrawingView.this.getWidth();
            //Log.d(TAG, "screen: " + totalHeight + "x" + totalWidth);
            final int heightInBlocks = map.length;
            final int widthInBlocks = map[0].length;
            final int blockHeight = (int) Math.floor(((double) totalHeight) / heightInBlocks);
            final int blockWidth = (int) Math.floor(((double) totalWidth) / widthInBlocks);
            Paint redPaint = new Paint();
            Paint whitePaint = new Paint();
            redPaint.setStrokeWidth(1);
            whitePaint.setStrokeWidth(1);
            redPaint.setColor(Color.RED);
            whitePaint.setColor(Color.WHITE);
            while (mRunning) {
                canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;
                    canvas.drawColor(Color.GREEN);
                    for (int i = 0; i < map.length; i++) {
                        for (int j = 0; j < map[i].length; j++) {
                            Paint paint = null;
                            if (map[i][j] == '-') {
                                paint = redPaint;
                            } else {
                                paint = whitePaint;
                            }
                            canvas.drawRect(j * blockWidth, i * blockHeight,
                                    j * blockWidth + blockWidth, i * blockHeight + blockHeight, paint);
                        }
                    }
                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
