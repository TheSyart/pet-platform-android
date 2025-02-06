package com.example.petstore.dialogUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.petstore.R;


public class ShowShoppingDialog {
    private AlertDialog dialog;
    private ImageView shopping_image, close;
    private TextView shopping_title, profile, shopping_price;


    public void showDialog(Context context, String imageUrl, String name, Double price, String description) {
        // 创建一个AlertDialog.Builder对象
        // 创建BottomSheetDialog对象
        // 创建一个AlertDialog.Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 获取布局解析器
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.shopping_dialog, null);

        // 初始化dialog内控件
        close = dialogView.findViewById(R.id.close);
        shopping_image = dialogView.findViewById(R.id.shopping_image);
        shopping_title = dialogView.findViewById(R.id.shopping_title);
        shopping_price = dialogView.findViewById(R.id.shopping_price);
        profile = dialogView.findViewById(R.id.profile);

        Glide.with(context)
                .load(imageUrl)
                .into(shopping_image);
        shopping_title.setText(name);
        shopping_price.setText(String.valueOf(price));
        profile.setText(description);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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

}

