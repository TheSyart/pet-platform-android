package com.example.petstore.dialogUtils;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import com.example.petstore.R;
import com.example.petstore.activity.HomeActivity;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.pojo.Dynamics;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.utils.LoadPicture;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

public class ShowDynamicsDialog{
    private int contentLength = 0;
    private int MAX_SIZE = 200;
    private String max_content;
    private ImageView dynamics_image;
    private String image_pass;
    private AlertDialog dialog;
    private NetRequest netRequest;

    public void showDialog(HomeActivity homeActivity, Context context, ActivityResultLauncher<Intent> launcher) {

        //发送动态到服务器后返回数据
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            @Override
            public void successPost(String requestType, String code, String msg, String data) {
                if (requestType.equals("postNewDynamicsRequest")) {
                    // 刷新动态
                    Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
                    homeActivity.reload();
                }
                if (requestType.equals("upLoadFile")) {
                    //照片上传，返回的地址
                    System.out.println("照片返回地址：" + data);
                    image_pass = data;

                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postNewDynamicsRequest")) {
                    System.out.println("requestType：" + requestType);
                    System.out.println("errorMessage：" + errorMessage);
                }
                if (requestType.equals("upLoadFile")) {
                    System.out.println("requestType：" + requestType);
                    System.out.println("errorMessage：" + errorMessage);
                }
            }
        });





        // 创建一个AlertDialog.Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 获取布局解析器
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.post_dynamics_dialog, null);

        // 初始化dialog内控件
        EditText dynamics_content = dialogView.findViewById(R.id.dynamics_content);
        dynamics_image = dialogView.findViewById(R.id.dynamics_image);
        TextView dynamics_content_num = dialogView.findViewById(R.id.dynamics_content_num);
        Button dynamics_send = dialogView.findViewById(R.id.dynamics_send);

        // 启动本地图库，选择照片
        dynamics_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadPicture loadPicture = new LoadPicture();
                loadPicture.getPictureByPhone(launcher, "Images");

            }
        });



        //监控文本内长度，超过两百禁止输入
        dynamics_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                contentLength -= count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contentLength += count;
                dynamics_content_num.setText(String.valueOf(contentLength));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == MAX_SIZE){
                    max_content = String.valueOf(s);
                    contentLength = s.length();
                } else if (s.length() > MAX_SIZE) {
                    //当直接从两百跳过(例如粘贴复制，打字法等),截取字符串200位。
                    max_content = String.valueOf(s).substring(0,MAX_SIZE);

                    //超过两百字，一直覆盖200字时的文本，使其内容保持两百不变
                    Toast.makeText(context, "已超过两百字", Toast.LENGTH_SHORT).show();
                    dynamics_content.setText(max_content);
                    contentLength = MAX_SIZE;
                    //使光标在最后
                    dynamics_content.setSelection(MAX_SIZE);
                }else {
                    contentLength = s.length();
                }


            }
        });

        dynamics_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dynamics dynamics = new Dynamics();

                // 获取数据
                SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("loggedInUsername", null);


                String content = dynamics_content.getText().toString();

                dynamics.setContent(content);
                dynamics.setUsername(username);

                // 判断是否有照片地址
                if (image_pass != null){
                    dynamics.setImage_path(image_pass);
                }else {
                    dynamics.setImage_path(null);
                }

                //向服务器发送新动态
                netRequest.postNewDynamicsRequest("insertDynamics", dynamics);

                //发布成功，关闭对话框
                dialog.dismiss();
                Toast.makeText(context, "动态发布成功！", Toast.LENGTH_SHORT).show();

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

    public void setImage(byte[] imageBytes, Context context){
        System.out.println("展示选择图片：" + imageBytes);
        // 将字节数组转换为 Bitmap 并显示在 ImageView 上
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            dynamics_image.setImageBitmap(bitmap);
        }

        netRequest.upLoadFile(imageBytes,"dynamics", ".jpg", "common");
    }

}

