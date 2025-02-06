package com.example.petstore.dialogUtils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.petstore.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ShowDatePiker {
    private BottomSheetDialog dialog;
    private String selectedDate;
    private OnDateSelectedListener dateSelectedListener;

    public interface OnDateSelectedListener {
        void onDateSelected(String date);
    }

    public void showDialog(Context context, OnDateSelectedListener listener) {
        this.dateSelectedListener = listener;

        // 创建一个AlertDialog.Builder对象
        // 创建BottomSheetDialog对象
        dialog = new BottomSheetDialog(context);

        // 获取布局解析器
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.datepicker, null);

        // 初始化dialog内控件
        ImageView close = dialogView.findViewById(R.id.close);
        DatePicker date_piker = dialogView.findViewById(R.id.date_piker);
        Button submit = dialogView.findViewById(R.id.submit);

        // 设置 DatePicker 的最大日期为今天
        long currentDate = System.currentTimeMillis();
        date_piker.setMaxDate(currentDate);

        //初始化显示今天日期
        selectedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));


        //关闭对话框
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // 日期选择监听
        date_piker.init(date_piker.getYear(), date_piker.getMonth(), date_piker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // 每次更改日期时触发
                        selectedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                    }
                });

        // 设置提交按钮的点击事件
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate != null && dateSelectedListener != null) {
                    // 获取当前日期
                    String todayString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    LocalDate today = LocalDate.parse(todayString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    //selectedDate 是用户选择的日期
                    String selectedDateString = selectedDate;
                    LocalDate selectedDate = LocalDate.parse(selectedDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    // 比较日期
                    if (today.isBefore(selectedDate)) {
                        Toast.makeText(context, "未出生，请重新选择!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "选择成功: " + selectedDateString, Toast.LENGTH_SHORT).show();
                        dateSelectedListener.onDateSelected(selectedDateString); // 回调选中的日期
                        dialog.dismiss();
                    }
                }

            }
        });

        // 将自定义布局设置到对话框中
        dialog.setContentView(dialogView);

        // 设置背景和其他属性
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0)); // 去除边框

        // 显示对话框
        dialog.show();
    }
}
