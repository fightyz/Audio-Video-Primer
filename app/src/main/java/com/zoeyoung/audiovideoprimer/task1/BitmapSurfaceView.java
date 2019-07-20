package com.zoeyoung.audiovideoprimer.task1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jakewharton.rxbinding2.view.RxView;
import com.zoeyoung.audiovideoprimer.R;

import io.reactivex.disposables.CompositeDisposable;

public class BitmapSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final String TAG = "BitmapSurfaceView";

    // SurfaceHolder 负责维护 SurfaceView 上绘制的内容
    private SurfaceHolder holder;
    private Canvas canvas;
    private Bitmap bitmap;
    private Paint mPaint;
    private boolean threadFlag;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public BitmapSurfaceView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        mPaint = new Paint();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Thread thread = new Thread(this,"draw-bitmap-thread");
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
        threadFlag = true;
//        thread.start();
        drawBitmap();
        mCompositeDisposable.add(RxView.touches(this)
                .filter(event -> MotionEvent.ACTION_DOWN == event.getAction())
                .subscribe(event -> {
            int x = (int) event.getX();
            int y = (int) event.getY();

            Canvas canvas = holder.lockCanvas(new Rect(x - 50, y - 50,
                    x + 50, y + 50));
            canvas.save();
            canvas.rotate(30, x, y);
            mPaint.setColor(Color.RED);
            canvas.drawRect(x - 40, y - 40, x, y, mPaint);
            canvas.restore();
            mPaint.setColor(Color.GREEN);
            canvas.drawRect(x, y, x + 40, y + 40, mPaint);
            holder.unlockCanvasAndPost(canvas);

            holder.lockCanvas(new Rect(0, 0, 0, 0));
            holder.unlockCanvasAndPost(canvas);

            holder.lockCanvas(new Rect(0, 0, 0, 0));
            holder.unlockCanvasAndPost(canvas);
        }));
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
            canvas = holder.lockCanvas(); // 锁定整个 SurfaceView 返回的画布可以用来编辑上面的像素，并可以绘制 Bitmap 对象
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
