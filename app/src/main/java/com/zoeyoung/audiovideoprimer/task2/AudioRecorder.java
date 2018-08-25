package com.zoeyoung.audiovideoprimer.task2;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 实现录音
 */
public class AudioRecorder {

    // 音频输入 - 麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

    // 采用频率
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样频率一般共分为22.05kHz, 44.1kHz, 48Khz
    private final static int AUDIO_SAMPLE_RATE = 16000;

    // 声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;

    // 编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;

    // 录音对象
    private AudioRecord audioRecord;

    // 录音状态
    private Status status = Status.STATUS_NOT_READY;

    // 文件名
    private String fileName;

    private List<String> filesName = new ArrayList<>();

    // 线程池
    private ExecutorService mExecutorService;

    public AudioRecorder() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    public void createAudio(String fileName, int audioSource, int sampleRateInHz, int channelConfig,
                            int audioFormat) {
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        this.fileName = fileName;
    }

    public enum Status {
        // 未开始
        STATUS_NOT_READY,
        // 预备
        STATUS_READY,
        // 录音
        STATUS_START,
        // 暂停
        STATUS_PAUSE,
        // 停止
        STATUS_STOP
    }
}
