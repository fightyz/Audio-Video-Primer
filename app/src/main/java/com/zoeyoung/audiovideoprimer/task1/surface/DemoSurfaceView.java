package com.zoeyoung.audiovideoprimer.task1.surface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * ${TODO}
 *
 * @author joe.yang@dji.com
 * @date 2019-07-20 17:42
 */
public class DemoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "DemoSurfaceView";

    private LoopThread mThread;

    public DemoSurfaceView(Context context) {
        super(context);
        init();
    }

    private void init() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mThread = new LoopThread(holder, getContext());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        mThread.isRunning = true;
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread.isRunning = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行绘制的绘制线程
     */
    class LoopThread extends Thread {
        SurfaceHolder mSurfaceHolder;
        Context mContext;
        boolean isRunning;
        float radius = 10f;
        Paint mPaint;

        public LoopThread(SurfaceHolder surfaceHolder, Context context) {
            mSurfaceHolder = surfaceHolder;
            mContext = context;
            mPaint = new Paint();
            mPaint.setColor(Color.BLUE);
            mPaint.setStyle(Paint.Style.STROKE);
        }

        @Override
        public void run() {
            Canvas c = null;
            while (isRunning) {
                synchronized (mSurfaceHolder) {
                    try {
                        c = mSurfaceHolder.lockCanvas(null);
                        doDraw(c);
//                        Thread.sleep(1);
                    }
//                    catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    finally {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        public void doDraw(Canvas c) {
            c.drawColor(Color.WHITE);
            c.translate(200, 200);
            c.drawCircle(0, 0, radius++, mPaint);
            if (radius > 100) {
                radius = 10f;
            }
        }
    }
}
