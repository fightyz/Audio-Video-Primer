package com.zoeyoung.audiovideoprimer.task2;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.zoeyoung.audiovideoprimer.R;
import com.zoeyoung.audiovideoprimer.util.AudioVideoPrimerLog;
import com.zoeyoung.audiovideoprimer.util.IOUtil;
import com.zoeyoung.audiovideoprimer.util.ToastUtil;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;

public class AudioRecordActivity extends Activity {
    private static final String TAG = "AudioRecordActivity";

    // 指定音频源
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;

    // 指定采样率 （MediaRecorder 的采样率通常是 8000Hz AAC 的通常是44100Hz。采样率设置为 44100，目前为常用采样率，官方文档表示这个值可以兼容所有的设置）
    private static final int SAMPLE_RATE = 44100;

    // 指定捕获音频的声道数目。
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_CONFIGURATION_MONO; // 单声道

    // 存放的目录路径名称
    private static final String PATH_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AudioRecordFile";

    // 保存的音频文件名
    private static final String FILE_NAME = "audiorecordtest.pcm";

    // 指定音频量化位数，在 AudioFormat 类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    // 因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    // 指定缓冲区大小。调用 AudioRecord 类的 getMinBufferSize 方法可以获得。
    private int mBufferSizeInBytes;

    private File mRecordingFile; // 存储 AudioRecord 录下来的文件
    private volatile boolean isRecording; // 表示正在录音
    private AudioRecord mAudioRecord; // 声明 AudioRecord 对象
    private File mFileRoot; // 文件目录
    private Thread mThread;

    private DataOutputStream mDataOutputStream;
    private Button mStartRecordBtn;
    private Button mStopRecordBtn;
    private Button mStartPlayBtn;
    private Button mStopPlayBtn;
    private Button mPlayWAVBtn;
    private Button mPcm2WAVBtn;
    private Button mPlayAACBtn;
    private Button mPcm2AACBtn;
    private TextView mEncodeProcessTv;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        initData();
        initView();
        initEvent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioVideoPrimerLog.i(TAG, "onPause, composite clear");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

    private void initData() {
        // 初始化数据
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        // 创建 AudioRecorder 对象
        mAudioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, mBufferSizeInBytes);
        // 创建文件夹
        mFileRoot = new File(PATH_NAME);
        if (!mFileRoot.exists()) {
            mFileRoot.mkdirs();
        }
    }

    private void initView() {
        mStartRecordBtn = findViewById(R.id.bt_start_record);
        mStopRecordBtn = findViewById(R.id.bt_stop_record);
        mStartPlayBtn = findViewById(R.id.bt_play_record);
        mStopPlayBtn = findViewById(R.id.bt_stop_play_record);
        mPcm2WAVBtn = findViewById(R.id.bt_pcm2wav);
        mPlayWAVBtn = findViewById(R.id.bt_play_wav);
        mPcm2AACBtn = findViewById(R.id.bt_pcm2aac);
        mPlayAACBtn = findViewById(R.id.bt_play_aac);
        mEncodeProcessTv = findViewById(R.id.tv_encode_process);
    }

    private void initEvent() {
        mCompositeDisposable.add(RxView.clicks(mStartRecordBtn)
                .doOnEach(o -> AudioVideoPrimerLog.i(TAG, "start record " + o))
                .subscribe(v -> {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                            PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                    PackageManager.PERMISSION_GRANTED) {
                        AudioVideoPrimerLog.e(TAG, "check permission fails");
                        ActivityCompat.requestPermissions(AudioRecordActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                    } else {
                        startRecord();
                    }
                }));
        mCompositeDisposable.add(RxView.clicks(mStopRecordBtn)
                .doOnEach(o -> AudioVideoPrimerLog.i(TAG, "stop record " + o))
                .subscribe(v -> stopRecord()));
        mCompositeDisposable.add(RxView.clicks(mStartPlayBtn)
                .subscribe(v -> startPlay()));
//        mCompositeDisposable.add(RxView.clicks(mStopPlayBtn).subscribe(v -> stopPlay()));
//        mCompositeDisposable.add(RxView.clicks(mPcm2WAVBtn).subscribe(v -> pcm2wav()));
//        mCompositeDisposable.add(RxView.clicks(mPlayWAVBtn).subscribe(v -> playWav()));
//        mCompositeDisposable.add(RxView.clicks(mPcm2AACBtn).subscribe(v -> pcm2aac()));
//        mCompositeDisposable.add(RxView.clicks(mPlayAACBtn).subscribe(v -> playAAC()));
    }

    /**
     * 开始录音
     */
    private void startRecord() {
        if (AudioRecord.ERROR_BAD_VALUE == mBufferSizeInBytes || AudioRecord.ERROR == mBufferSizeInBytes) {
            throw new RuntimeException("Unable to getMinBufferSize");
        } else {
            destroyThread();
            isRecording = true;
            if (mThread == null) {
                mThread = new Thread(recordTask);
                mThread.start();
            }
        }
    }

    /**
     * 停止录音
     */
    private void stopRecord() {
        isRecording = false;
        // 停止录音，回收 AudioRecord 对象，释放内存
        if (mAudioRecord != null) {
            // 初始化成功
            if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                mAudioRecord.stop();
            }
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    /**
     * 开始播放录音
     */
    private void startPlay() {
        String path = mFileRoot + File.separator + FILE_NAME;
        AudioTrackManager.getInstance().startPlay(path);
    }

    /**
     * 销毁线程方法
     */
    private void destroyThread() {
        try {
            isRecording = false;
            if (null != mThread && Thread.State.RUNNABLE == mThread.getState()) {
                try {
                    Thread.sleep(500);
                    mThread.interrupt();
                } catch (Exception e) {
                    mThread = null;
                }
            }
            mThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mThread = null;
        }
    }

    private Runnable recordTask = () -> {
        isRecording = true;
        createFile();

        // 判断 AudioRecord 未初始化，停止录音的时候释放了，状态就为 STATE_UNINITIALIZED
        if (mAudioRecord.getState() == mAudioRecord.STATE_UNINITIALIZED) {
            initData();
        }

        // 最小缓冲区
        byte[] buffer = new byte[mBufferSizeInBytes];
        // 获取到文件的数据流
        try {
            mDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mRecordingFile)));

            // 开始录音
            mAudioRecord.startRecording();
            AudioVideoPrimerLog.i(TAG, "start recording " + mAudioRecord.getRecordingState());
            // getRecordingState 获取当前 AudioRecording 是否正在采集数据的状态
            while (isRecording && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                int bufferReadResult = mAudioRecord.read(buffer, 0, mBufferSizeInBytes);
                mDataOutputStream.write(buffer, 0, bufferReadResult);
            }
        } catch (FileNotFoundException e) {
            AudioVideoPrimerLog.e(TAG, "record task " + e);
            e.printStackTrace();
        } catch (IOException e) {
            AudioVideoPrimerLog.e(TAG, "record task " + e);
            e.printStackTrace();
        } finally {
//            stopRecord();
            IOUtil.close(mDataOutputStream);
        }
    };

    private void createFile() {
        //创建一个流，存放从AudioRecord读取的数据
        mRecordingFile = new File(mFileRoot, FILE_NAME);
        AudioVideoPrimerLog.i(TAG, "create file " + mRecordingFile);
        if (mRecordingFile.exists()) {//音频文件保存过了删除
            mRecordingFile.delete();
        }
        try {
            mRecordingFile.createNewFile();//创建新文件
        } catch (IOException e) {
            e.printStackTrace();
            AudioVideoPrimerLog.e(TAG, "create file fail " + e);
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecord();
            } else {
                ToastUtil.show("未授权");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
