package com.zoeyoung.audiovideoprimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding2.view.RxView;
import com.zoeyoung.audiovideoprimer.task1.surface.doublebuffer.DoubleBufferActivity;

import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCompositeDisposable.add(RxView.clicks(findViewById(R.id.task_1_tv)).subscribe(v -> {
            Intent intent = new Intent(MainActivity.this, DoubleBufferActivity.class);
            startActivity(intent);
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
