package com.zoeyoung.audiovideoprimer.task1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zoeyoung.audiovideoprimer.R;

public class BitmapSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final String TAG = "BitmapSurfaceView";
    private boolean threadFlag;
    private SurfaceHolder holder;
    private Canvas canvas;
    private Bitmap bitmap;

    public BitmapSurfaceView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Thread thread = new Thread(this,"draw-bitmap-thread");
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
        threadFlag = true;
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged: format=" + format
            + " width=" + width
            + " height=" + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        threadFlag = false;
    }

    @Override
    public void run() {
        while (threadFlag) {
            drawBitmap();
        }
    }

    private void drawBitmap() {
        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                canvas.drawBitmap(bitmap, 0, 0, null);
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
