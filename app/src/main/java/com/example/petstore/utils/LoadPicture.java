package com.example.petstore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

public class LoadPicture {
    private byte[] imageBytes;
    private byte[] videoBytes;

    // 新增方法，接收 ActivityResultLauncher
    public void getPictureByPhone(ActivityResultLauncher<Intent> launcher, String type) {
        // 打开本地图库
        openGallery(launcher, type);
    }

    private void openGallery(ActivityResultLauncher<Intent> launcher, String type) {
        Intent intent = null;
        if (type.equals("Images")) {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        } else if (type.equals("Video")) {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        }

        launcher.launch(intent);
    }

    public void onActivityResult(int resultCode, Intent data, Context context, String type) {
        if (resultCode == Activity.RESULT_OK && data!= null && data.getData()!= null) {
            Uri uri = data.getData();
            if ("Images".equals(type)) {
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    imageBytes = getBytesFromInputStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if ("Video".equals(type)) {
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    videoBytes = getBytesFromInputStream(inputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer))!= -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public byte[] getVideoBytes() {
        return videoBytes;
    }
}