package com.zoeyoung.audiovideoprimer.task1.surface.doublebuffer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zoeyoung.audiovideoprimer.util.AudioVideoPrimerLog;

/**
 * ${TODO}
 *
 * @author joe.yang@dji.com
 * @date 2019-08-03 12:33
 */
public class DoubleBufferActivity extends AppCompatActivity {
    private static final String TAG = "DoubleBufferActivity";
    DoubleBufferFlashSurfaceView mDoubleBufferFlashSurfaceView;
    DoubleBufferSurfaceView mDoubleBufferSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDoubleBufferSurfaceView = new DoubleBufferSurfaceView(this);
        setContentView(mDoubleBufferSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioVideoPrimerLog.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioVideoPrimerLog.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        AudioVideoPrimerLog.i(TAG, "onStop");
    }
}
