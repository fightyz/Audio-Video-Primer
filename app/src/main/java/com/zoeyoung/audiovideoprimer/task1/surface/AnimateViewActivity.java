package com.zoeyoung.audiovideoprimer.task1.surface;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.View;

/**
 * ${TODO}
 *
 * @author joe.yang@dji.com
 * @date 2019-07-20 17:33
 */
public class AnimateViewActivity extends Activity {

    private SurfaceView mDemoSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDemoSurfaceView = new DemoSurfaceView(this);
        setContentView(mDemoSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    class AnimateView extends View {
        float radius = 10;
        Paint paint;

        public AnimateView(Context context) {
            super(context);
            paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.translate(200, 200);
            canvas.drawCircle(0, 0, radius++, paint);
            if (radius > 100) {
                radius = 10;
            }
            invalidate();
        }
    }
}
