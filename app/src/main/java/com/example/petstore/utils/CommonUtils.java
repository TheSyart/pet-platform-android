package com.example.petstore.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import java.io.IOException;
import java.util.HashMap;

public class CommonUtils {
    public String convertMillisToTimeFormat(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public Bitmap getVideoPreviewFrame(MediaMetadataRetriever retriever) throws IOException {
        return retriever.getFrameAtTime(0);  // 获取视频第一帧，0表示时间戳为0的位置，也就是开始位置
    }

    public long getVideoLength(MediaMetadataRetriever retriever) throws IOException {
        String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(durationStr);
    }

    public boolean checkPhoneNumber(String phone) {
        // 正则表达式匹配中国大陆手机号：11位，以1开头，第二位为3-9
        String regex = "^1[3-9]\\d{9}$";
        return phone.matches(regex);
    }
}
