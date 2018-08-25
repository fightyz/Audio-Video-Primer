package com.zoeyoung.audiovideoprimer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zoeyoung.audiovideoprimer.task1.DrawBitmapActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button drawBitmapActivityBtn = (Button) findViewById(R.id.draw_bitmap_activity_btn);
        drawBitmapActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DrawBitmapActivity.class);
                startActivity(intent);
            }
        });
    }
}
