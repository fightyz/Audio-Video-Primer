package com.zoeyoung.audiovideoprimer.util;

import android.widget.Toast;

import com.zoeyoung.audiovideoprimer.APP;

public class ToastUtil {
    public static void show(String message) {
        Toast.makeText(APP.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void show(int message) {
        Toast.makeText(APP.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
