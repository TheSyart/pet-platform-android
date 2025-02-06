package com.example.petstore.dialogUtils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petstore.Adapter.MyPetAdapter;
import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.interfaces.MyPetServiceInterface;
import com.example.petstore.pojo.MyPet;
import com.example.petstore.utils.JwtUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import org.json.JSONException;
import java.util.ArrayList;

public class ShowMyPetDialog {
    private BottomSheetDialog dialog;
    private NetRequest netRequest;
    private MyPetAdapter myPetAdapter;
    private RecyclerView myPet_recycler;
    private Button add;
    private MyPetServiceInterface listener;  // 用于回调接口

    public void setOnPetSelectedListener(MyPetServiceInterface listener) {
        this.listener = listener;  // 设置回调监听
    }

    public void showDialog(Context context, ArrayList<MyPet> myPetArrayList) {
        // 创建一个AlertDialog.Builder对象
        // 创建BottomSheetDialog对象
        dialog = new BottomSheetDialog(context);
        setNetResponse(context);
        // 获取布局解析器
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.mypet_dialog, null);

        // 初始化dialog内控件
        myPet_recycler = dialogView.findViewById(R.id.myPet_recycler);
        add = dialogView.findViewById(R.id.add);


        myPetAdapter = new MyPetAdapter(context, myPetArrayList, new MyPetAdapter.OnPetSelectedListener() {
            @Override
            public void onPetSelected(MyPet myPet) {
                if (listener != null) {
                    listener.onPetSelected(myPet);  // 调用回调方法
                }
                dialog.dismiss(); // 关闭对话框
            }
        });

        myPet_recycler.setAdapter(myPetAdapter);

        // 设置横向布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        myPet_recycler.setLayoutManager(layoutManager);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.startAddMyPetDialog();  // 调用回调方法
                }
            }
        });


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));//去除边框
        // 将自定义布局设置到对话框中
        dialog.setContentView(dialogView);
        // 显示对话框
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }

    //接受网络请求返回数据
    private void setNetResponse(Context context){
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {

            }

            @Override
            public void failurePost(String requestType, String errorMessage) {

            }
        });
    }
}
