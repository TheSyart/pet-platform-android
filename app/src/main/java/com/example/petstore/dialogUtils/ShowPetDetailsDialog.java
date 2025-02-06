package com.example.petstore.dialogUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.petstore.R;
import com.example.petstore.pojo.Encyclopedia;

public class ShowPetDetailsDialog {
    private AlertDialog dialog;
    public void showDialog(Context context, Encyclopedia petDetails) {

        // 创建一个AlertDialog.Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 获取布局解析器
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.encyclopedia_petdetails_dialog, null);

        // 初始化dialog内控件
        TextView petNameShow = dialogView.findViewById(R.id.petName);
        ImageView petImageShow = dialogView.findViewById(R.id.petImage);
        TextView contentShow = dialogView.findViewById(R.id.content);
        TextView petWeightShow = dialogView.findViewById(R.id.petWeight);
        TextView petHeightShow = dialogView.findViewById(R.id.petHeight);
        TextView petLifeShow = dialogView.findViewById(R.id.petLife);
        TextView petOriginShow = dialogView.findViewById(R.id.petOrigin);
        TextView petShapeShow = dialogView.findViewById(R.id.petShape);
        TextView anotherNameShow = dialogView.findViewById(R.id.anotherName);
        TextView petPriceShow = dialogView.findViewById(R.id.petPrice);
        //设置对话框内各个控件值
        petNameShow.setText(petDetails.getPetName());
        Glide.with(context)
                .load(petDetails.getPetImage())
                .into(petImageShow);

        contentShow.setText(petDetails.getContent());
        petWeightShow.setText(petDetails.getPetWeight());
        petHeightShow.setText(petDetails.getPetHeight());
        petLifeShow.setText(petDetails.getPetLife());
        petOriginShow.setText(petDetails.getPetOrigin());
        petShapeShow.setText(petDetails.getPetShape());
        anotherNameShow.setText(petDetails.getAnotherName());
        petPriceShow.setText(petDetails.getPetPrice());

        // 将自定义布局设置到对话框中
        builder.setView(dialogView);

        // 创建AlertDialog对象并显示
        dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));//去除边框
        dialog.show();

    }
}
