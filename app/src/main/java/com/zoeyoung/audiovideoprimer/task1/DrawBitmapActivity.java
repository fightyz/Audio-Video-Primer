package com.zoeyoung.audiovideoprimer.task1;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zoeyoung.audiovideoprimer.R;

import java.util.ArrayList;
import java.util.List;

public class DrawBitmapActivity extends Activity {

    private ViewPager viewPager;
    private List<View> viewList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BitmapSurfaceView bitmapSurfaceView = new BitmapSurfaceView(this);

        ImageView bitmapImageView = new ImageView(this);
        bitmapImageView.setImageResource(R.drawable.lenna);

        View BitmapView = new BitmapView(this);

        setContentView(R.layout.activity_draw_bitmap);

        viewPager = (ViewPager) findViewById(R.id.draw_bitmap_view_pager);
        viewList = new ArrayList<>();
        viewList.add(bitmapImageView);
        viewList.add(bitmapSurfaceView);
        viewList.add(BitmapView);
        PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewList.get(position));
//                super.destroyItem(container, position, object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };

        viewPager.setAdapter(adapter);
    }
}
