package com.example.petstore.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petstore.R;
import com.example.petstore.activity.ShoppingActivity;
import com.example.petstore.anotherActivity.GDMapActivity;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.dialogUtils.ShowPayDialog;
import com.example.petstore.pojo.Address;
import com.example.petstore.pojo.OrderInfo;
import com.example.petstore.utils.JwtUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressHolder> {
    private NetRequest netRequest;
    private List<Address> addressList;
    private Context context;
    private BottomSheetDialog dialog;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Activity activity;
    private int selectedPosition = -1; // 记录选中的位置
    private long lastClickTime = 0; // 防抖时间
    private AddressListener addressListener;

    // 定义接口
    public interface AddressListener {
        void defaultChange(Address address);
    }

    public AddressAdapter(ActivityResultLauncher<Intent> activityResultLauncher, Activity activity,
                          Context context, List<Address> addressList, BottomSheetDialog dialog, AddressListener addressListener) {
        this.activityResultLauncher = activityResultLauncher;
        this.activity = activity;
        this.context = context;
        this.addressList = addressList;
        this.addressListener = addressListener;
        this.dialog = dialog;

        setNetResponse(context);
        for (Address address : addressList) {
            if (address.getDefaultAddress() == 0){
                selectedPosition = addressList.indexOf(address);
            }
        }

    }

    @NonNull
    @Override
    public AddressAdapter.AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.address_item, parent, false);
        return new AddressAdapter.AddressHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull AddressAdapter.AddressHolder holder, int position) {
        holder.select_address.setOnCheckedChangeListener(null); // 移除旧的监听器

        if (addressList.get(position).getDefaultAddress() == 0){
            holder.select_address.setChecked(true);
        } else {
            holder.select_address.setChecked(false);
        }

        // 点击事件处理
        holder.select_address.setOnCheckedChangeListener((buttonView, isChecked) -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < 500) { // 设置防抖时间
                return;
            }
            lastClickTime = currentTime;

            if (isChecked) {
                // 如果点击的不是当前默认地址
                if (position != selectedPosition) {
                    int oldPosition = selectedPosition;
                    selectedPosition = position;

                    // 更新状态
                    defaultAddress(addressList.get(position).getId());

                    // 有无默认地址的判断
                    if (oldPosition == -1) {
                        postNetRequest(null, addressList.get(selectedPosition).getId());
                    } else {
                        postNetRequest(addressList.get(oldPosition).getId(), addressList.get(selectedPosition).getId());
                    }
                    notifyDataSetChanged();
                    if (addressListener != null){
                        addressListener.defaultChange(addressList.get(position));
                    }
                    dialog.dismiss();
                }
            } else {
                // 取消选中当前默认地址
                if (position == selectedPosition) {
                    selectedPosition = -1;
                    addressList.get(position).setDefaultAddress(1); // 非默认
                    notifyDataSetChanged();
                    postNetRequest(addressList.get(position).getId(), null);
                }
            }
        });



        holder.modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Gson gson = new Gson();
                String addressJson = gson.toJson(addressList.get(position));

                Intent intent = new Intent(activity, GDMapActivity.class);
                intent.putExtra("type", "modify");
                intent.putExtra("address", addressJson);
                activityResultLauncher.launch(intent);
            }
        });

        holder.address.setText(addressList.get(position).getAddressTitle() + " " + addressList.get(position).getDoor());
        if (addressList.get(position).getGender() == 0){
            holder.name.setText(addressList.get(position).getName() + " 先生");
        } else {
            holder.name.setText(addressList.get(position).getName() + " 女士");
        }
        holder.phone.setText(String.valueOf(addressList.get(position).getPhone()));
        holder.itemView.setOnClickListener( v -> {
            if (addressListener != null){
                dialog.dismiss();
                addressListener.defaultChange(addressList.get(position));
            }

        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public void defaultAddress(String id) {
        for (Address address : addressList) {
            if (address.getId().equals(id)) {
                address.setDefaultAddress(0); // 设置为默认地址
            } else {
                address.setDefaultAddress(1); // 取消默认地址
            }
        }
    }



    public class AddressHolder extends RecyclerView.ViewHolder {
        TextView address;
        TextView name;
        TextView phone;
        ImageView modify;
        CheckBox select_address;

        public AddressHolder(View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            modify = itemView.findViewById(R.id.modify);
            select_address = itemView.findViewById(R.id.select_address);
        }
    }

    //接受网络请求返回数据
    private void setNetResponse(Context context) {
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("postAddressRequest")) {

                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postAddressRequest")) {

                }
            }
        });
    }

    private void postNetRequest(String oldId, String newId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("oldId", oldId);
            jsonObject.put("newId", newId);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // 将最终 JSON 对象转换为字符串
        String jsonPayload = jsonObject.toString();
        netRequest.postAddressRequest("updateDefaultAddress", jsonPayload );
    }
}

