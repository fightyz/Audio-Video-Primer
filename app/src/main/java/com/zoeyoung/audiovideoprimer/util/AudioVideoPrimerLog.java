package com.zoeyoung.audiovideoprimer.util;

import android.util.Log;

/**
 * ${TODO}
 *
 * @author joe.yang@dji.com
 * @date 2019-08-03 13:34
 */
public class AudioVideoPrimerLog {
    private static final String TAG = "AudioVideoPrimerLog";

    public static void i(String tag, String msg) {
        Log.i(TAG, "[" + tag + "] " + msg);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG, "[" + tag + "] " + msg);
    }
}
