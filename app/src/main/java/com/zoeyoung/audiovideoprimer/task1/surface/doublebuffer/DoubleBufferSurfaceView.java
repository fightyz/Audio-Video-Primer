package com.zoeyoung.audiovideoprimer.task1.surface.doublebuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zoeyoung.audiovideoprimer.util.AudioVideoPrimerLog;

import java.util.Random;

/**
 * 由于 Surface view 的双缓冲问题，因此这个 surface view 上绘制的图像会闪烁
 *
 * @author joe.yang@dji.com
 * @date 2019-08-03 12:34
 */
public class DoubleBufferSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "DoubleBufferSurfaceView";
    private SurfaceHolder mSurfaceHolder;
    private DoubleBufferThread mDoubleBufferThread;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Random mRandom;
    private int mCanvasWidth;
    private int mCanvasHeight;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Matrix identityMatrix;

    public DoubleBufferSurfaceView(Context context) {
        super(context);
    }

    public DoubleBufferSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DoubleBufferSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AudioVideoPrimerLog.i(TAG, "onAttachedToWindow");
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AudioVideoPrimerLog.i(TAG, "onDetachedFromWindow");
        mSurfaceHolder.removeCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        AudioVideoPrimerLog.i(TAG, "surfaceCreated");
        mCanvasWidth = getWidth();
        mCanvasHeight = getHeight();
        mBitmap = Bitmap.createBitmap(mCanvasWidth, mCanvasHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas();
        mCanvas.setBitmap(mBitmap);
        identityMatrix = new Matrix();

        mRandom = new Random();
        mDoubleBufferThread = new DoubleBufferThread(this, 500);
        mDoubleBufferThread.setRunning(true);
        mDoubleBufferThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        AudioVideoPrimerLog.i(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        AudioVideoPrimerLog.i(TAG, "surfaceDestroyed");

        boolean retry = true;
        mDoubleBufferThread.setRunning(false);
        while (retry) {
            try {
                mDoubleBufferThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        AudioVideoPrimerLog.i(TAG, "onDraw");
    }

    private void drawContent(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        int x = mRandom.nextInt(w - 1);
        int y = mRandom.nextInt(h - 1);
        int r = mRandom.nextInt(255);
        int g = mRandom.nextInt(255);
        int b = mRandom.nextInt(255);
        mPaint.setColor(0xff000000 + (r << 16) + (g << 8) + b);

        mCanvas.drawCircle(x, y, 10, mPaint);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(mBitmap, identityMatrix, null);
    }

    public void updateStates() {

    }

    public void updateSurfaceView() {
        Canvas canvas = null;
        boolean valid = mSurfaceHolder.getSurface().isValid();

        if (valid) {
            try {
                canvas = mSurfaceHolder.lockCanvas();
                if (canvas != null) {
                    updateStates();
                    drawContent(canvas);
                }
            } finally {
                if (canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
