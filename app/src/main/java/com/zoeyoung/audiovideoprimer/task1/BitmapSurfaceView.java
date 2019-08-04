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

    int count = 0;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Thread thread = new Thread(this, "draw-bitmap-thread");
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
        threadFlag = true;
//        thread.start();
        drawBitmap();
        // lock - unlock 一次，两块缓冲区做一次 flip，让两块缓冲区中的内容保持一致。
        // 如果去掉这次 flip 可以看到第一次点击时有黑块（因为 backedBuffer 还没有内容）
        holder.lockCanvas(new Rect(0, 0, 0, 0));
        holder.unlockCanvasAndPost(canvas);
        mCompositeDisposable.add(RxView.touches(this)
                .filter(event -> MotionEvent.ACTION_DOWN == event.getAction())
                .subscribe(event -> {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    canvas = holder.lockCanvas(new Rect(x - 50, y - 50,
                            x + 50, y + 50));
                    Log.i(TAG, "click canvas clipBounds " + canvas.getClipBounds());

                    canvas.save();
                    canvas.rotate(30, x, y);
                    mPaint.setColor(Color.RED);
                    canvas.drawRect(x - 40, y - 40, x, y, mPaint);
                    canvas.restore();
                    mPaint.setColor(Color.GREEN);
                    canvas.drawRect(x, y, x + 40, y + 40, mPaint);
                    holder.unlockCanvasAndPost(canvas);

                    count++;

                    // 这里需要做一次 flip，将两个缓冲区的内容同步。如果不同步，则会出现这次点击时的透明角
                    // 会覆盖上一次所绘制的色块。如果不 delay 100ms，则这次的 flip 可能不会生效，这个可能涉及到
                    // 底层 flip 的时间间隔，在这个时间间隔内再做 flip 可能不生效
                    BitmapSurfaceView.this.postDelayed(() -> {
                        canvas = holder.lockCanvas(new Rect(0, 0, 0, 0));
                        holder.unlockCanvasAndPost(canvas);
                    }, 100);
                }));
    }

    private void drawColor() {
        try {
            canvas = holder.lockCanvas(); // 锁定整个 SurfaceView 返回的画布可以用来编辑上面的像素，并可以绘制 Bitmap 对象
            if (canvas != null) {
                canvas.drawColor(getResources().getColor(android.R.color.holo_blue_bright));
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
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
            canvas = holder.lockCanvas(new Rect(50, 50, 100, 100)); // 锁定整个 SurfaceView 返回的画布可以用来编辑上面的像素，并可以绘制 Bitmap 对象
            Log.i(TAG, "1 canvas clipBounds " + canvas.getClipBounds());
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
