package com.example.petstore.dialogUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.petstore.R;
import com.example.petstore.anotherActivity.CustomerServiceActivity;


public class VoiceDialog {
    private Dialog dialog;
    private Context context;
    private ImageView dialog_iv_icon;
    private AnimationDrawable anim;
    private CustomerServiceActivity activity;
    private Handler handler;


    public VoiceDialog(Context context, CustomerServiceActivity activity) {
        this.context = context;
        this.activity = activity;
        init();
    }

    /**
     * Dialog初始化方法
     */
    private void init() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_voice_manager, null);
        dialog.setContentView(inflate);
        // 后续代码保持不变，继续获取ImageView控件、设置动画资源等操作
        dialog_iv_icon = dialog.findViewById(R.id.dialog_iv_icon);
        dialog_iv_icon.setBackgroundResource(R.drawable.voice_anim);
        anim = (AnimationDrawable) dialog_iv_icon.getBackground();
        anim.start();
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM /**| Gravity.TOP*/);
        //lp.x = 100; // 新位置X坐标
        lp.y = 200; // 新位置Y坐标
        lp.width = dp2px(260); // 宽度
        lp.height = dp2px(260); // 高度
        lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);

        // 设置对话框开启监听器
        dialog.setOnShowListener(dialog -> {
            // 开启录音
            activity.startRecord();
            // 使用 Handler 延迟 10 秒关闭对话框
            handler = new Handler();
            handler.postDelayed(() -> {
                dismiss();
            }, 10000);
        });

        // 设置对话框关闭监听器
        dialog.setOnDismissListener(dialog -> {
            // 关闭录音
            activity.stopRecord();
            if (handler!= null) {
                handler.removeCallbacksAndMessages(null);
            }
            Toast.makeText(context, "语音最多为10秒", Toast.LENGTH_SHORT).show();
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));//去除边框
        dialog.show();
    }

    private int dp2px(int dpValue) {
        return (int) context.getResources().getDisplayMetrics().density * dpValue;
    }

    public void show() {
        if (dialog!= null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog!= null) {
            anim.stop();
            dialog.dismiss();
            anim = null;
            dialog = null;
        }
    }
}