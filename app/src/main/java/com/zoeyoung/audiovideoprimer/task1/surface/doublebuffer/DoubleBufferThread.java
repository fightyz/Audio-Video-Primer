package com.zoeyoung.audiovideoprimer.task1.surface.doublebuffer;

/**
 * ${TODO}
 *
 * @author joe.yang@dji.com
 * @date 2019-08-03 12:37
 */
public class DoubleBufferThread extends Thread {
    private static final String TAG = "DoubleBufferThread";
    private volatile boolean running = false;
    private DoubleBufferFlashSurfaceView mFlashSurfaceView;
    private DoubleBufferSurfaceView mSurfaceView;
    private long sleepTime;

    public DoubleBufferThread(DoubleBufferFlashSurfaceView flashSurfaceView, long sleepTime) {
        super();
        mFlashSurfaceView = flashSurfaceView;
        this.sleepTime = sleepTime;
    }

    public DoubleBufferThread(DoubleBufferSurfaceView flashSurfaceView, long sleepTime) {
        super();
        mSurfaceView = flashSurfaceView;
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
                if (mFlashSurfaceView != null) {
                    mFlashSurfaceView.updateSurfaceView();
                }
                if (mSurfaceView != null) {
                    mSurfaceView.updateSurfaceView();
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
