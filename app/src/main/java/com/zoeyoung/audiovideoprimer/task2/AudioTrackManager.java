package com.zoeyoung.audiovideoprimer.task2;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Process;

import com.zoeyoung.audiovideoprimer.util.AudioVideoPrimerLog;
import com.zoeyoung.audiovideoprimer.util.IOUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AudioTrackManager {
    private static final String TAG = "AudioTrackManager";
    private AudioTrack mAudioTrack;
    private DataInputStream mDis; // 播放文件的数据流
    private Thread mRecordThread;
    private boolean isStart = false;
    private volatile static AudioTrackManager mInstance;

    // 音频流类型
    private static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
    // 指定采样率
    private static final int SAMPLE_RATE = 44100;
    // 捕获音频的声道数目。
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    // 音频量化位数
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private static final int MODE = AudioTrack.MODE_STREAM;
    // 指定缓冲区大小
    private int mMinBufferSize;

    private AudioTrackManager() {
        initData();
    }

    private void initData() {
        // 根据采样率、采样精度，单双声道来得到 frame 的大小
        mMinBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        mAudioTrack = new AudioTrack(STREAM_TYPE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, mMinBufferSize, MODE);
    }

    public static AudioTrackManager getInstance() {
        if (mInstance == null) {
            synchronized (AudioTrackManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioTrackManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 启动播放
     *
     * @param path
     */
    public void startPlay(String path) {
        try {
            setPath(path);
            startThread();
        } catch (Exception e) {
            AudioVideoPrimerLog.e(TAG, "start play fail " + e);
        }
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        try {
            destroyThread(); // 销毁线程
            if (mAudioTrack != null) {
                if (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                    mAudioTrack.stop(); // 停止播放
                }
                mAudioTrack.release(); // 释放 audioTrack 资源
            }
        } finally {
            if (mDis != null) {
                IOUtil.close(mDis);
            }
        }

    }

    private void destroyThread() {
        try {
            isStart = false;
            if (null != mRecordThread && Thread.State.RUNNABLE == mRecordThread.getState()) {
                try {
                    Thread.sleep(500);
                    mRecordThread.interrupt();
                } catch (Exception e) {
                    mRecordThread = null;
                }
            }
            mRecordThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRecordThread = null;
        }
    }

    private void setPath(String path) throws Exception {
        File file = new File(path);
        mDis = new DataInputStream(new FileInputStream(file));
    }

    private void startThread() {
        destroyThread();
        isStart = true;
        if (mRecordThread == null) {
            mRecordThread = new Thread(recordRunnable);
            mRecordThread.start();
        }
    }

    /**
     * 播放任务
     */
    private Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                byte[] tempBuffer = new byte[mMinBufferSize];
                int readCount = 0;
                AudioVideoPrimerLog.i(TAG, "read data mDis available " + mDis.available());
                while (mDis.available() > 0) {
                    readCount = mDis.read(tempBuffer);
                    if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                        continue;
                    }
                    if (readCount != 0 && readCount != -1) {
                        if (mAudioTrack.getState() == mAudioTrack.STATE_UNINITIALIZED) {
                            initData();
                        }
                        mAudioTrack.play();
                        mAudioTrack.write(tempBuffer, 0, readCount);
                    }
                }
                // 播放完就停止播放
                stopPlay();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };
}
