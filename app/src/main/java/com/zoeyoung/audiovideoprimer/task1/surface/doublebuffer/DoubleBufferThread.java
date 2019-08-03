package com.zoeyoung.audiovideoprimer.task1.surface.doublebuffer;

import android.util.Log;

/**
 * ${TODO}
 *
 * @author joe.yang@dji.com
 * @date 2019-08-03 12:37
 */
public class DoubleBufferThread extends Thread {
    private static final String TAG = "DoubleBufferThread";
    private volatile boolean running = false;
    private DoubleBufferSurfaceView mSurfaceView;
    private long sleepTime;

    public DoubleBufferThread(DoubleBufferSurfaceView surfaceView, long sleepTime) {
        super();
        mSurfaceView = surfaceView;
        this.sleepTime = sleepTime;
    }

    public void setRunning(boolean r) {
        running = r;
    }

    @Override
    public void run() {
        while (running) {
            try {
                sleep(sleepTime);
                mSurfaceView.updateSurfaceView();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
