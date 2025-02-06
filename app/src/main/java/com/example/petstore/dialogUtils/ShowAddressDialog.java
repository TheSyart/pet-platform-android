package com.example.petstore.dialogUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petstore.Adapter.CustomerServerAdapter;
import com.example.petstore.R;
import com.example.petstore.anotherActivity.GDMapActivity;
import com.example.petstore.activity.ShoppingActivity;
import com.example.petstore.Adapter.AddressAdapter;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.pojo.Address;
import com.example.petstore.utils.JwtUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import org.json.JSONException;
import java.util.List;

public class ShowAddressDialog implements AddressAdapter.AddressListener{
    private BottomSheetDialog dialog;
    private NetRequest netRequest;
    private AddressAdapter addressAdapter;
    private RecyclerView address_recycler;
    private ImageView close;
    private Button add;
    private ShowAddressListener showAddressListener;

    // 定义接口
    public interface ShowAddressListener {

        void change(Address address);
    }


    // 在 showDialog 方法中使用 showPayDialog
    public void showDialog(ActivityResultLauncher<Intent> activityResultLauncher, Activity activity, Context context, List<Address> addressList, ShowAddressListener showAddressListener) {
        this.showAddressListener = showAddressListener;

        // 创建一个AlertDialog.Builder对象
        // 创建BottomSheetDialog对象
        dialog = new BottomSheetDialog(context);
        setNetResponse(context);
        // 获取布局解析器
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.address_dialog, null);

        // 初始化dialog内控件
        address_recycler = dialogView.findViewById(R.id.address_recycler);
        add = dialogView.findViewById(R.id.add);
        close = dialogView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        addressAdapter = new AddressAdapter(activityResultLauncher, activity, context, addressList, dialog, this);
        address_recycler.setAdapter(addressAdapter);
        address_recycler.setLayoutManager(new LinearLayoutManager(context));


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(activity, GDMapActivity.class);
                intent.putExtra("type", "add");
                activityResultLauncher.launch(intent);
            }
        });


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));//去除边框

        // 将自定义布局设置到对话框中
        dialog.setContentView(dialogView);
        // 显示对话框
        dialog.show();
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

    @Override
    public void defaultChange(Address address) {
        if (showAddressListener != null){
            showAddressListener.change(address);
        }
    }
}
