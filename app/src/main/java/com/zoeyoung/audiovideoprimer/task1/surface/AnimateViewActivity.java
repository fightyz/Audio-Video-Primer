package com.zoeyoung.audiovideoprimer.task1.surface;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;

/**
 * ${TODO}
 *
 * @author joe.yang@dji.com
 * @date 2019-07-20 17:33
 */
public class AnimateViewActivity extends Activity {

    private SurfaceView mDemoSurfaceView;
    private AnimateView mAnimateView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDemoSurfaceView = new DemoSurfaceView(this);
        mAnimateView = new AnimateView(this);
        setContentView(mAnimateView);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
