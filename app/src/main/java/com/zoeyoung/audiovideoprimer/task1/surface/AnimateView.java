package com.zoeyoung.audiovideoprimer.task1.surface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * ${TODO}
 *
 * @author joe.yang@dji.com
 * @date 2019-08-03 12:04
 */
class AnimateView extends View {
    private static final String TAG = "AnimateView";
    float radius = 10;
    Paint paint;
    long lastSystemTime;

    public AnimateView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long currentTime = System.currentTimeMillis();
        Log.d(TAG, "doDraw time " + (currentTime - lastSystemTime));
        lastSystemTime = currentTime;
        canvas.translate(200, 200);
        canvas.drawCircle(0, 0, radius++, paint);
        if (radius > 100) {
            radius = 10;
        }
        invalidate();
    }
}
