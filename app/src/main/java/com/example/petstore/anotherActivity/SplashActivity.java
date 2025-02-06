package com.example.petstore.anotherActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.petstore.R;
import com.example.petstore.activity.LoginActivity;

import android.os.CountDownTimer;

public class SplashActivity extends AppCompatActivity {
    private TextView countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        countdown = findViewById(R.id.countdown);

        // 创建倒计时
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 每秒更新一次文本内容，显示剩余秒数
                countdown.setText("跳转倒计时：" + ((millisUntilFinished / 1000) + 1) + "秒");
            }

            @Override
            public void onFinish() {
                // 倒计时结束后执行跳转
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}

