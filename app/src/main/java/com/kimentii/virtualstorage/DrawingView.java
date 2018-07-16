package com.kimentii.virtualstorage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawingView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawingThread mDrawingThread;

    public DrawingView(Context context) {
        super(context);
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
            while (mRunning) {
                canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;
                    canvas.drawColor(Color.GREEN);
                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
