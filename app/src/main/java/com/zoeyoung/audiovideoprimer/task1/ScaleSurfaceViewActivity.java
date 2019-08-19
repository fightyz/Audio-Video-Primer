package com.zoeyoung.audiovideoprimer.task1;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;

import com.zoeyoung.audiovideoprimer.R;
import com.zoeyoung.audiovideoprimer.util.DensityUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ScaleSurfaceViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_surface_view);
//        SurfaceView view = findViewById(R.id.demo_surface_view);
        View view = findViewById(R.id.demo_surface_view);
        ObjectAnimator xValueAnimator = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 2);
        ObjectAnimator yValueAnimator = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), 2);
        xValueAnimator.setDuration(1000);
        yValueAnimator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(xValueAnimator, yValueAnimator);
        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
            animatorSet.start();
        });
    }
}
