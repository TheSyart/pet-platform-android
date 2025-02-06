package com.example.petstore.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.easy.view.EasyEditText;
import com.example.petstore.R;
import com.example.petstore.activity.RegisterActivity;
import com.example.petstore.dao.NetRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DialogUtils {
    private static NetRequest netRequest;

    public static void showMessageDialog(Context context, String title, String message, String hint) {
        new AlertDialog.Builder(context)
                .setTitle(title) // 设置标题
                .setMessage(message + "\n\n" + hint) // 添加提示信息
                .setPositiveButton("确定", (dialog, which) -> {
                    // 点击确定后的操作
                    dialog.dismiss();
                })
                .show();
    }


    // 性别选择框
    private static final String[] gender = new String[]{"男", "女"};
    private static int selectedIndex;

    public interface GenderSelectCallback {
        void onGenderSelected(int index);
    }

    public static void selectGender(Context context, int currentIndex, GenderSelectCallback callback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setIcon(R.drawable.female_sign)
                .setTitle("请选择你的性别")
                // 第一个参数:设置单选的资源;第二个参数:设置默认选中哪几项
                .setSingleChoiceItems(gender, currentIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // 记录选择的索引
                        selectedIndex = i;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 这里假设selectedIndex是记录选择索引的变量，
                        // 在实际中可能需要在类中定义成员变量来正确获取
                        if (callback != null) {
                            callback.onGenderSelected(selectedIndex);
                        }
                    }
                }).create();
        alertDialog.show();
    }


    // 带有输入框的对话框
    public interface InputDialogCallback {
        void onInputEntered(String input);
    }

    public static void showInputDialog(Context context, String title, String hint, String content, InputDialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final EditText input = new EditText(context);
        input.setHint(hint);
        input.setText(content);
        input.setBackgroundResource(R.drawable.et_bg);
        // 设置文本对齐方式为居中
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        // 设置布局参数示例
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(params);
        builder.setView(input);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInput = input.getText().toString().trim();
                if (callback != null) {
                    callback.onInputEntered(userInput);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }


    // 定义日期选择回调接口
    public interface DateSelectCallback {
        void onDateSelected(String data);
    }

    public static void showDatePickerDialog(Context context, DateSelectCallback callback) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String formattedDate = dateFormat.format(selectedDate.getTime());
                        if (callback != null) {
                            callback.onDateSelected(formattedDate);
                        }
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }


    // 定义密码回调接口
    public interface PasswordCallback {
        void onPasswordInput(String newPassword);
    }

    private static AlertDialog dialog;
    private static Boolean oldV = false;
    private static Boolean newV = false;
    private static Boolean ackV = false;

    public static void showPasswordDialog(Context context, String oldPassword, PasswordCallback callback) {
        // 创建一个AlertDialog.Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 获取布局解析器
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.password_dialog, null);

        // 初始化dialog内控件
        EditText old_password = dialogView.findViewById(R.id.old_password);
        EditText new_password = dialogView.findViewById(R.id.new_password);
        EditText ackPassword = dialogView.findViewById(R.id.ackPassword);

        ImageView old_sign = dialogView.findViewById(R.id.old_sign);
        ImageView new_sign = dialogView.findViewById(R.id.new_sign);
        ImageView ack_sign = dialogView.findViewById(R.id.ack_sign);

        TextView old_tip = dialogView.findViewById(R.id.old_tip);
        TextView new_tip = dialogView.findViewById(R.id.new_tip);
        TextView ack_tip = dialogView.findViewById(R.id.ack_tip);

        Button close = dialogView.findViewById(R.id.close);
        Button ack = dialogView.findViewById(R.id.ack);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldV && newV && ackV) {
                    callback.onPasswordInput(String.valueOf(new_password.getText()));
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "请正确修改密码!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // 为旧密码添加 TextWatcher
        old_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 在文本变化前调用
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                old_sign.setVisibility(View.VISIBLE);
                // 可以在这里实时检查输入长度或格式，比如最小长度要求
                if (oldPassword.equals(str)) {
                    old_tip.setVisibility(View.INVISIBLE);
                    oldV = true;
                    old_sign.setImageResource(R.drawable.right);
                } else {
                    oldV = false;
                    old_tip.setVisibility(View.VISIBLE);
                    old_tip.setText("密码错误!");
                    old_sign.setImageResource(R.drawable.error);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 在文本变化后调用

            }
        });

        // 为新密码添加 TextWatcher
        new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new_sign.setVisibility(View.VISIBLE);
                if (s.length() < 6 || s.length() > 16) {
                    new_tip.setVisibility(View.VISIBLE);
                    new_tip.setText("密码长度应该在6-16位!");
                    newV = false;
                    new_sign.setImageResource(R.drawable.error);
                } else {
                    newV = true;
                    new_tip.setVisibility(View.INVISIBLE);
                    new_sign.setImageResource(R.drawable.right);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 为确认密码添加 TextWatcher
        ackPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ack_sign.setVisibility(View.VISIBLE);
                // 检查确认密码是否与新密码一致
                if (s.toString().equals(new_password.getText().toString())) {
                    ackV = true;
                    ack_tip.setVisibility(View.INVISIBLE);
                    ack_sign.setImageResource(R.drawable.right);
                } else {
                    ackV = false;
                    ack_tip.setText("密码不一致!");
                    ack_tip.setVisibility(View.VISIBLE);
                    ack_sign.setImageResource(R.drawable.error);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 将自定义布局设置到对话框中
        builder.setView(dialogView);

        // 创建AlertDialog对象并显示
        dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));//去除边框
        dialog.show();
    }


    // 定义密码回调接口
    public interface PhoneCallback {
        void onPhoneInput(String newPhone, String code);
    }

    private static AlertDialog phoneDialog;

    private static CommonUtils commonUtils = new CommonUtils();
    private static Boolean phoneV = false;

    public static void showPhoneDialog(Context context, String oldPhone, PhoneCallback callback) {
        // 创建一个AlertDialog.Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 获取布局解析器
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.phone_dialog, null);

        setNetResponse(context);

        // 初始化dialog内控件
        EditText phone = dialogView.findViewById(R.id.phone);
        ImageView phone_sign = dialogView.findViewById(R.id.phone_sign);
        TextView get_code = dialogView.findViewById(R.id.get_code);
        TextView tip = dialogView.findViewById(R.id.tip);

        LinearLayoutCompat main_container = dialogView.findViewById(R.id.main_container);
        LinearLayoutCompat code_container = dialogView.findViewById(R.id.code_container);

        EasyEditText code_input = dialogView.findViewById(R.id.code_input);
        Button send = dialogView.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onPhoneInput(String.valueOf(phone.getText()), code_input.getText());
                phoneDialog.dismiss();
            }
        });

        get_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNetRequest(String.valueOf(phone.getText()));
                main_container.setVisibility(View.GONE);
                code_container.setVisibility(View.VISIBLE);
            }
        });


        // 为phone添加 TextWatcher
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 在文本变化前调用
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                phone_sign.setVisibility(View.VISIBLE);
                // 可以在这里实时检查输入长度或格式，比如最小长度要求
                if (str.equals(oldPhone)) {
                    phoneV = false;
                    tip.setVisibility(View.VISIBLE);
                    tip.setText("与原手机号一致!");
                    phone_sign.setImageResource(R.drawable.error);
                } else if (commonUtils.checkPhoneNumber(str)) {
                    tip.setVisibility(View.INVISIBLE);
                    phoneV = true;
                    phone_sign.setImageResource(R.drawable.right);
                } else {
                    phoneV = false;
                    tip.setVisibility(View.VISIBLE);
                    tip.setText("请输入正确手机号!");
                    phone_sign.setImageResource(R.drawable.error);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 在文本变化后调用

            }
        });


        // 将自定义布局设置到对话框中
        builder.setView(dialogView);

        // 创建AlertDialog对象并显示
        phoneDialog = builder.create();
        phoneDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        phoneDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));//去除边框
        phoneDialog.show();
    }

    private ProgressDialog progressDialog;

    // loading的对话框
    public void showLoadingDialog(Context context, String content) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(content);
        progressDialog.show();
    }

    public void closeLoadingDialog(){
        progressDialog.dismiss();
    }





    private static void postNetRequest(String phone) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phoneNumber", phone);
            jsonObject.put("sendType", "change");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        String jsonPayload = jsonObject.toString();
        netRequest.postCustomerRequest("sendSms", jsonPayload);
    }

    //接受网络请求返回数据
    private static void setNetResponse(Context context) {
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("postCustomerRequest")) {
                    Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postCustomerRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


