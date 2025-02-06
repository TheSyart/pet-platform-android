package com.example.petstore.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.lang.ref.WeakReference;

public class RecorderAudioManagerUtils {
    private static final String TAG = "RecorderAudioManagerUtils";
    private WeakReference<Context> weakReference;
    // 用于存储录音数据的字节数组输出流，替代原来的本地文件
    private ByteArrayOutputStream byteArrayOutputStream;
    private boolean isRecording;
    // 16K采集率
    int sampleRateInHz = 16000;
    // 16Bit
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private static volatile RecorderAudioManagerUtils mInstance;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    // 线程池，这里可根据实际情况调整参数，与外部传入的线程池区分开，用于内部可能的异步操作
    private ThreadPoolExecutor internalThreadPoolExecutor = new ThreadPoolExecutor(
            2, 4,
            1, TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(8),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());
    private boolean isResourceInUse = false;

    public static RecorderAudioManagerUtils getInstance() {
        if (mInstance == null) {
            synchronized (RecorderAudioManagerUtils.class) {
                if (mInstance == null) {
                    mInstance = new RecorderAudioManagerUtils();
                }
            }
        }
        return mInstance;
    }

    // 定义一个接口，用于将录音数据回调给主活动
    public interface RecordAudioListener {
        void onRecordData(byte[] audioData);

        void playAudioFinish();
    }

    private RecordAudioListener recordAudioListener;

    // 设置录音数据监听器的方法，供外部传入监听器实例
    public void setRecordDataListener(RecordAudioListener listener) {
        this.recordAudioListener = listener;
    }

    public void startRecord(WeakReference<Context> weakReference) {
        synchronized (this) {
            isResourceInUse = true;
            this.weakReference = weakReference;
            byteArrayOutputStream = new ByteArrayOutputStream();
            isRecording = true;
            try {
                int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_IN_STEREO, audioEncoding);
                if (ActivityCompat.checkSelfPermission(weakReference.get(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "无录音权限，无法开始录音");
                    return;
                }

                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, AudioFormat.CHANNEL_IN_STEREO, audioEncoding, bufferSize);
                short[] buffer = new short[bufferSize];
                DataOutputStream dos = new DataOutputStream(byteArrayOutputStream);

                audioRecord.startRecording();
                Log.e(TAG, "开始录音");

                internalThreadPoolExecutor.execute(() -> {
                    try {
                        while (isRecording) {
                            int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                            for (int i = 0; i < bufferReadResult; i++) {
                                dos.writeShort(buffer[i]);
                            }
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "录音数据读取写入异常", e);
                    } finally {
                        try {
                            dos.close();
                        } catch (IOException e) {
                            Log.e(TAG, "关闭输出流异常", e);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "录音失败");
                showToast("录音失败");
            }
        }
    }

    /**
     * 停止录音方法，添加停止录音的相关逻辑，并在停止时进行数据回调（如果有监听器的话）
     */
    public void stopRecord() {
        synchronized (this) {
            isRecording = false;
            if (audioRecord != null) {
                audioRecord.stop();
            }
            if (recordAudioListener != null && byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "关闭字节数组输出流失败");
                }
                byte[] pcmData = byteArrayOutputStream.toByteArray();
                recordAudioListener.onRecordData(pcmData);
            }
            isResourceInUse = false;
        }
    }


    /**
     * 根据后端返回url播放Pcm流
     */
    public void playPcmData(WeakReference<Context> weak, String url) {
        synchronized (this) {
            weakReference = weak;
            isResourceInUse = true;
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                // 网络URL情况，这里需要使用网络请求相关库（如HttpURLConnection或者OkHttp等）来获取输入流，以下是使用HttpURLConnection的简单示例框架，实际使用中可能需要更多错误处理和配置优化
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                connection.setRequestMethod("GET");
                bis = new BufferedInputStream(connection.getInputStream());

                // 设置音频相关参数，与录音时的参数保持一致（采样率、声道配置、音频编码格式等），确保能正确播放
                int channelConfiguration = AudioFormat.CHANNEL_OUT_STEREO;
                int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfiguration, audioEncoding);
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfiguration,
                        audioEncoding, bufferSize, AudioTrack.MODE_STREAM);
                if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                    audioTrack.play();
                    byte[] buffer = new byte[bufferSize];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        try {
                            audioTrack.write(buffer, 0, bytesRead);
                        } catch (IllegalStateException e) {
                            Log.e(TAG, "音频写入异常，停止播放并释放资源", e);
                            stopPcmDataAndAnim();
                            break;
                        }
                    }
                    // 播放完成后，停止并释放AudioTrack资源
                    stopPcmDataAndAnim();

                } else {
                    Log.e(TAG, "AudioTrack初始化失败");
                    if (audioTrack != null) {
                        audioTrack.release();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                stopPcmDataAndAnim();
                Log.e(TAG, "播放语音失败");
                showToast("播放语音失败");
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "关闭输入流失败");
                }
            }
            isResourceInUse = false;
        }
    }

    /**
     * 停止播放Pcm流
     */
    public void stopPcmData() {
        if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
            audioTrack.stop();
            audioTrack.release();
        }
    }

    /**
     * 因播放完成原因，停止播放语音
     */
    public void stopPcmDataAndAnim() {
        synchronized (this) {
            if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                audioTrack.stop();
                audioTrack.release();
            }
            if (recordAudioListener != null) {
                recordAudioListener.playAudioFinish();
            }
        }
    }

    public void pauseAudio() {
        synchronized (this) {
            if (audioRecord != null) {
                audioRecord.stop();
            }
            if (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.stop();
            }
        }
    }

    public void releaseAudio() {
        synchronized (this) {
            if (isResourceInUse) {
                Log.w(TAG, "资源正在被使用，暂不能释放，等待合适时机再尝试");
                return;
            }
            isResourceInUse = true;
            if (audioRecord != null) {
                audioRecord.release();
            }
            if (audioTrack != null) {
                audioTrack.release();
            }
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            if (internalThreadPoolExecutor != null) {
                internalThreadPoolExecutor.shutdown();
            }
            isResourceInUse = false;
        }
    }

    private void showToast(String msg) {
        if (weakReference.get() != null) {
            handler.post(() -> Toast.makeText(weakReference.get(), msg, Toast.LENGTH_LONG).show());
        }
    }
}