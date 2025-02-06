package com.example.petstore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.dialogUtils.ShowDatePiker;
import com.example.petstore.utils.CheckNull;
import com.example.petstore.utils.JwtUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView countdownText;
    private EditText phone_input;
    private EditText vCode_input;
    private TextView birth;
    private TextView vCode_get;
    private Button register;
    private RadioGroup radioGroup;
    private ImageView backToLogin;
    private NetRequest netRequest;
    private String birth_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        init();

        netRequest = new NetRequest(new JwtUtils(), getApplicationContext(), new NetRequest.ResponseCallback() {
            @Override
            public void successPost(String requestType, String code, String msg, String data) {
                if (requestType.equals("postRegisterRequest")) {
                    Toast.makeText(RegisterActivity.this, data , Toast.LENGTH_SHORT).show();
                    //注册成功跳转到登录界面
                    if(data.equals("注册成功")) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }

                if (requestType.equals("postCustomerRequest")) {
                    vCode_get.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, data, Toast.LENGTH_SHORT).show();
                    if (data.equals("验证码成功发送")){
                        register.setEnabled(true);
                        vCode_get.setVisibility(View.GONE); // 倒计时开始后隐藏按钮,并显示倒计时
                        countdownText.setVisibility(View.VISIBLE);
                        startCountdown();
                    }

                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postRegisterRequest")) {
                    Toast.makeText(RegisterActivity.this, "注册失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                }

                if (requestType.equals("postCustomerRequest")) {
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void init() {
        //定义倒计时文本
        countdownText = findViewById(R.id.countdownText);

        backToLogin = findViewById(R.id.backToLogin);
        phone_input = findViewById(R.id.phone_input);
        vCode_input = findViewById(R.id.vCode_input);
        vCode_get = findViewById(R.id.vCode_get);
        birth = findViewById(R.id.birth);
        register = findViewById(R.id.register);
        radioGroup = findViewById(R.id.radioGroupGender);

        birth.setOnClickListener(this);
        backToLogin.setOnClickListener(this);
        vCode_get.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        String phone = phone_input.getText().toString();
        String gender = "";
        String code = vCode_input.getText().toString();


        //注册
        if (v.getId() == R.id.register) {
            if (selectedId == R.id.radioMale) {
                // 男性
                gender = "0";
            } else if (selectedId == R.id.radioFemale) {
                // 女性
                gender = "1";
            } else {
                // 未选择
                Toast.makeText(RegisterActivity.this, "请选择性别", Toast.LENGTH_SHORT).show();
                return;
            }

            // 调试输出
            Log.d("RegisterActivity", "Phone: " + phone);
            Log.d("RegisterActivity", "Code: " + code);
            Log.d("RegisterActivity", "Gender: " + gender);
            CheckNull checkNull = new CheckNull();
            if (checkNull.areFieldsEmpty(List.of(phone,code,gender))){
                Toast.makeText(RegisterActivity.this, "请完整填写信息！", Toast.LENGTH_SHORT).show();
            }else if (code.length() != 4){
                Toast.makeText(RegisterActivity.this, "请填入正确验证码！", Toast.LENGTH_SHORT).show();
            }else if (phone.length() != 11){
                Toast.makeText(RegisterActivity.this, "请填入正确手机号！", Toast.LENGTH_SHORT).show();
            }else {
                // 创建请求体对象
                String jsonPayload = "{\"phone\":\"" + phone + "\", \"code\":\"" + code + "\" , \"gender\":\"" + gender + "\", \"birth\":\"" + birth_input + "\"}";
                // 发送 POST 请求
                netRequest.postRegisterRequest("register", jsonPayload); // endpoint 改为你的后端注册接口
            }


        }

        //发送验证码
        if (v.getId() == R.id.vCode_get) {
            if (!phone.equals("") && phone != null && phone.length() == 11) {
                vCode_get.setEnabled(false);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("phoneNumber", phone);
                    jsonObject.put("sendType", "register");
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                String jsonPayload = jsonObject.toString();
                netRequest.postCustomerRequest("sendSms", jsonPayload);
            }else {
                Toast.makeText(RegisterActivity.this, "请填写正确手机号", Toast.LENGTH_SHORT).show();
            }
        }

        if (v.getId() == R.id.backToLogin){
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.birth){
            ShowDatePiker showDatePiker = new ShowDatePiker();
            showDatePiker.showDialog(RegisterActivity.this, new ShowDatePiker.OnDateSelectedListener() {
                @Override
                public void onDateSelected(String date) {
                    // 在这里处理接收到的日期
                    birth_input = date;
                    birth.setText(birth_input);

                }
            });
        }
    }


    //手机验证码发送，六十秒冷却
    private void startCountdown() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                countdownText.setText("重新发送" + millisUntilFinished / 1000 + " 秒");
            }

            public void onFinish() {
                vCode_get.setVisibility(View.VISIBLE); // 倒计时结束后启用按钮,并隐藏倒计时
                countdownText.setVisibility(View.GONE);
            }

        }.start();
    }


}
