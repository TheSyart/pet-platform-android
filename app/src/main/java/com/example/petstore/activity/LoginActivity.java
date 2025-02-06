package com.example.petstore.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.petstore.R;
import com.example.petstore.component.LoginAnimation;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.utils.AndroidBug5497Workaround;
import com.example.petstore.utils.CheckNull;
import com.example.petstore.utils.DialogUtils;
import com.example.petstore.utils.JwtUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private LoginAnimation loginAnimation;
    private JwtUtils jwtUtils;
    private TextView countdownText;
    private TextView sendSms;
    private EditText phoneNumber;
    private EditText code;
    private EditText username;
    private EditText password;
    private TextView register;
    private Button login;
    private NetRequest netRequest;
    //登录方式判断
    private String loginStatus = "phone";
    private String userText;
    private String passwordText;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View progress;
    private View mInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_activity);
        init();
    }

    public void init() {
        // 控制软键盘
        AndroidBug5497Workaround.assistActivity(this);
        // 保存数据
         sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
         editor = sharedPreferences.edit();


        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);


        //定义倒计时文本
        countdownText = findViewById(R.id.countdownText);

        // 定义 ViewSwitcher 和切换按钮
        ViewSwitcher viewSwitcher = findViewById(R.id.switcher);
        TextView switchPhone = findViewById(R.id.switchPhone);
        TextView switchPassword = findViewById(R.id.switchPassword);

        // 设置点击事件
        switchPhone.setOnClickListener(v -> {
            loginStatus = "phone";
            login.setEnabled(false);
            viewSwitcher.showNext(); // 切换到验证码登录
            switchPhone.setVisibility(View.GONE); // 隐藏当前按钮
            switchPassword.setVisibility(View.VISIBLE); // 显示切换到密码登录的按钮
        });

        switchPassword.setOnClickListener(v -> {
            loginStatus = "password";
            login.setEnabled(true);
            viewSwitcher.showPrevious(); // 切换回密码登录
            switchPassword.setVisibility(View.GONE); // 隐藏当前按钮
            switchPhone.setVisibility(View.VISIBLE); // 显示切换到验证码登录的按钮
        });





        jwtUtils =new JwtUtils();
        sendSms = findViewById(R.id.sendSms);
        phoneNumber = findViewById(R.id.phoneNumber);
        code = findViewById(R.id.code);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        sendSms.setOnClickListener(this);
        register.setOnClickListener(this);
        login.setOnClickListener(this);

        // 使用同一个 PostRequest 对象
        netRequest = new NetRequest(new JwtUtils(), getApplicationContext(), new NetRequest.ResponseCallback() {
            @Override
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                // 登录校验
                if (requestType.equals("postLoginRequest")) {
                    if ("200".equals(code)) {
                        JSONObject jsonObject = new JSONObject(data);
                        String usename = jsonObject.getString("username");
                        String name = jsonObject.getString("name");
                        String id = jsonObject.getString("id");

                        // 在登录成功后设置username和name
                        editor.putString("loggedInUsername", usename);
                        editor.putString("loggedInName", name);
                        editor.putString("loggedInId", id);
                        editor.apply();
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        // 登录成功，跳转到主界面
                        Intent intent = new Intent(LoginActivity.this, PetPlatformActivity.class);
                        startActivity(intent);
                    }

                }

                //向手机发送验证码的结果
                if (requestType.equals("postCustomerRequest")){
                    sendSms.setEnabled(true);
                    Toast.makeText(LoginActivity.this, data, Toast.LENGTH_SHORT).show();
                    if ("200".equals(code)){
                        login.setEnabled(true);
                        sendSms.setVisibility(View.GONE);
                        countdownText.setVisibility(View.VISIBLE);
                        startCountdown();
                    }
                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                loginAnimation.recovery();
                if (requestType.equals("postLoginRequest")) {
                    Toast.makeText(LoginActivity.this, "登录失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("postCustomerRequest")){
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void onClick(View view) {
        CheckNull checkNull = new CheckNull();
        userText = username.getText().toString();
        passwordText = password.getText().toString();
        String phoneText = phoneNumber.getText().toString();
        String codeText = code.getText().toString();

        //跳转注册界面
        if (view.getId() == R.id.register) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }

        //向手机发送验证码
        if (view.getId() == R.id.sendSms){
            if (!phoneText.equals("") && phoneText != null && phoneText.length() == 11) {
                sendSms.setEnabled(false);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("phoneNumber", phoneText);
                    jsonObject.put("sendType", "login");
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                String jsonPayload = jsonObject.toString();
                netRequest.postCustomerRequest("sendSms", jsonPayload);

            }else {
                Toast.makeText(LoginActivity.this, "请填写正确手机号", Toast.LENGTH_SHORT).show();
            }

        }

        //登录校验
        if (view.getId() == R.id.login) {
            // 计算出控件的高与宽
            float mWidth = login.getMeasuredWidth();
            float mHeight = login.getMeasuredHeight();
            loginAnimation = new LoginAnimation(mInputLayout, progress);
            loginAnimation.inputAnimator(mWidth, mHeight);


//            Intent intent = new Intent(LoginActivity.this, TestActivity.class);
//            startActivity(intent);
            if (loginStatus.equals("password")){
                if (checkNull.areFieldsEmpty(List.of(userText,passwordText))) {
                    Toast.makeText(this, "请填写账号或密码", Toast.LENGTH_SHORT).show();
                }else {
                    //发送后端数据，校验登录
                    String jsonPayload = "{\"username\":\"" + userText + "\", \"password\":\"" + passwordText + "\"}";
                    netRequest.postLoginRequest("customerLogin", jsonPayload, "password");
                }

            } else if (loginStatus.equals("phone")) {
                if (checkNull.areFieldsEmpty(List.of(phoneText,codeText))) {
                    Toast.makeText(this, "请填写手机号或验证码！", Toast.LENGTH_SHORT).show();
                }else {
                    //发送后端数据，校验登录
                    String jsonPayload = "{\"phoneNumber\":\"" + phoneText + "\", \"code\":\"" + codeText + "\"}";
                    netRequest.postLoginRequest("customerLogin", jsonPayload, "phone");
                }

            }


        }
    }

    //手机验证码发送，六十秒冷却
    private void startCountdown() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                countdownText.setText("重新发送" + millisUntilFinished / 1000 + " 秒");
            }

            public void onFinish() {
                sendSms.setVisibility(View.VISIBLE); // 倒计时结束后启用按钮,并隐藏倒计时
                countdownText.setVisibility(View.GONE);
            }

        }.start();
    }
}
