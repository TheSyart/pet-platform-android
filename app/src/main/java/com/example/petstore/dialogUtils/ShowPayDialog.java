package com.example.petstore.dialogUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petstore.Adapter.AddressAdapter;
import com.example.petstore.Adapter.PayAdapter;
import com.example.petstore.R;
import com.example.petstore.activity.ShoppingActivity;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.pojo.Address;
import com.example.petstore.pojo.OrderInfo;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.utils.StringUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShowPayDialog implements ShowAddressDialog.ShowAddressListener {
    private BottomSheetDialog dialog;
    private PayAdapter payAdapter;
    private NetRequest netRequest;
    private BigDecimal sum = BigDecimal.ZERO; // 初始化为 0
    private String currentAddress;
    private Integer pickMethod;
    private List<Address> addressList = new ArrayList<>();
    private RadioGroup radioGroup;
    private RecyclerView pay_recycler;
    private ImageView select_address;
    private TextView total_price;
    private TextView address;
    private TextView name;
    private TextView phone;
    private EditText note, reserved_name, reserved_phone;
    private Button submit;
    private View address_info, people_info, reserved_info;
    private ShowAddressDialog showAddressDialog = new ShowAddressDialog();
    private ShowAddressDialog.ShowAddressListener showAddressListener = this;
    private StringUtils utils = new StringUtils();

    public void showDialog(ActivityResultLauncher<Intent> activityResultLauncher, ShoppingActivity shoppingActivity, Context context, String username, List<OrderInfo> orderInfoList) {
        // 创建一个AlertDialog.Builder对象
        // 创建BottomSheetDialog对象
        dialog = new BottomSheetDialog(context);

        // 获取布局解析器
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.pay_dialog, null);

        // 初始化dialog内控件
        radioGroup = dialogView.findViewById(R.id.radioGroup);
        pay_recycler = dialogView.findViewById(R.id.pay_recycler);
        select_address = dialogView.findViewById(R.id.select_address);
        total_price = dialogView.findViewById(R.id.total_price);
        address = dialogView.findViewById(R.id.address);
        name = dialogView.findViewById(R.id.name);
        phone = dialogView.findViewById(R.id.phone);
        note = dialogView.findViewById(R.id.note);
        submit = dialogView.findViewById(R.id.sumbit);
        reserved_name = dialogView.findViewById(R.id.reserved_name);
        reserved_phone = dialogView.findViewById(R.id.reserved_phone);

        people_info = dialogView.findViewById(R.id.people_info);
        address_info = dialogView.findViewById(R.id.address_info);
        reserved_info = dialogView.findViewById(R.id.reserved_info);

        payAdapter = new PayAdapter(context, orderInfoList);
        pay_recycler.setAdapter(payAdapter);
        pay_recycler.setLayoutManager(new LinearLayoutManager(context));

        setNetResponse(context, shoppingActivity);
        postNetRequest(username);

        address.setText("请选择地址");
        name.setText("");
        phone.setText("");

        for (OrderInfo orderInfo : orderInfoList) {
            sum = BigDecimal.ZERO;
            sum = sum.add(orderInfo.getSubtotal()); // 累加每个 subtotal
        }
        total_price.setText(String.valueOf(sum));

        select_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressDialog.showDialog(activityResultLauncher, shoppingActivity.getActivity(), context, addressList, showAddressListener);
            }
        });

        //取货方式选择
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioOnline){
                    pickMethod = 0;
                    people_info.setVisibility(View.VISIBLE);
                    address_info.setVisibility(View.VISIBLE);
                    reserved_info.setVisibility(View.GONE);
                }else if (checkedId == R.id.radioOutline){
                    pickMethod = 1;
                    people_info.setVisibility(View.GONE);
                    address_info.setVisibility(View.GONE);
                    reserved_info.setVisibility(View.VISIBLE);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickMethod == null){
                    Toast.makeText(context, "请选择取货方式", Toast.LENGTH_SHORT).show();
                    return;
                } else if(pickMethod == 1){
                    if (StringUtils.isEmpty(String.valueOf(reserved_name.getText())) ||
                            StringUtils.isEmpty(String.valueOf(reserved_phone.getText()))){
                        Toast.makeText(context, "请补全取货人信息", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("order_type", 0);
                    jsonObject.put("username", username);
                    jsonObject.put("pickMethod", pickMethod);
                    jsonObject.put("totalAmount", sum);
                    jsonObject.put("paymentMethod", 0);
                    jsonObject.put("addressId", currentAddress);
                    jsonObject.put("reservedName", reserved_name.getText());
                    jsonObject.put("reservedPhone", reserved_phone.getText());

                    // 创建 JSONArray 来存储 orderInfoList
                    JSONArray orderInfoArray = new JSONArray();
                    for (OrderInfo orderInfo : orderInfoList) {

                        JSONObject orderInfoJson = new JSONObject();
                        orderInfoJson.put("shopping_id", orderInfo.getId());
                        orderInfoJson.put("name", orderInfo.getName());          // name
                        orderInfoJson.put("price", orderInfo.getPrice());        // price
                        orderInfoJson.put("quantity", orderInfo.getQuantity());  // quantity
                        // 将每个 orderInfoJson 添加到 orderInfoArray
                        orderInfoArray.put(orderInfoJson);
                    }
                    jsonObject.put("orderInfoList", orderInfoArray);  // 将 orderInfoList 添加到 jsonObject
                    jsonObject.put("note", note.getText());

                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                // 将最终 JSON 对象转换为字符串
                String jsonPayload = jsonObject.toString();
                netRequest.postOrderRequest("insertOrder", jsonPayload);
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));//去除边框
        // 将自定义布局设置到对话框中
        dialog.setContentView(dialogView);
        // 显示对话框
        dialog.show();
    }

    //展示地址数据
    public void setAddressInfo(Address addressInfo) {
        currentAddress = addressInfo.getId();
        address.setText(addressInfo.getAddressTitle() + " " + addressInfo.getDoor());
        if (addressInfo.getGender() == 0){
            name.setText(addressInfo.getName() + " 先生");
        }else {
            name.setText(addressInfo.getName() + " 女士");
        }
        phone.setText(addressInfo.getPhone());

    }

    //发送网络请求
    private void postNetRequest(String username) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // 将最终 JSON 对象转换为字符串
        String jsonPayload = jsonObject.toString();
        netRequest.postAddressRequest("queryAddressByUsername", jsonPayload);
    }
    //接受网络请求返回数据
    private void setNetResponse(Context context, ShoppingActivity shoppingActivity){
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("postOrderRequest")) {
                    Toast.makeText(context, "订单支付成功!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    shoppingActivity.switchToOrder();
                }

                if (requestType.equals("postAddressRequest")) {
                    addressList.clear();
                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);

                    // 遍历 JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject json = jsonResponse.getJSONObject(i);
                        Address newAddress = new Address(
                                json.getString("id"),
                                json.getInt("defaultAddress"),
                                json.getString("name"),
                                json.getInt("gender"),
                                json.getString("phone"),
                                json.getString("addressTitle"),
                                json.getString("addressDetails"),
                                json.getString("door"),
                                json.getDouble("longitude"),
                                json.getDouble("latitude")
                        );
                        addressList.add(newAddress);

                        if (newAddress.getDefaultAddress() == 0){
                            setAddressInfo(newAddress);
                        }

                    }


                }

            }
            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postOrderRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                if (requestType.equals("postAddressRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    public void dismiss(){
        dialog.dismiss();
    }

    @Override
    public void change(Address address) {
        setAddressInfo(address);
    }
}
