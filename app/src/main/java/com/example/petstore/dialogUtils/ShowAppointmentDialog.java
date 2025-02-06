package com.example.petstore.dialogUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.petstore.Adapter.AppointmentAdapter;
import com.example.petstore.R;
import com.example.petstore.activity.OrderActivity;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.interfaces.MyPetServiceInterface;
import com.example.petstore.pojo.EmpDoctor;
import com.example.petstore.pojo.MyPet;
import com.example.petstore.pojo.OrderInfo;
import com.example.petstore.pojo.ServiceInfo;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.utils.StringUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShowAppointmentDialog {
    private StringUtils stringUtils = new StringUtils();
    private BottomSheetDialog dialog;
    private NetRequest netRequest;
    private AppointmentAdapter appointmentAdapter;
    private RecyclerView appointment_recycler;
    private ImageView pet_image;
    private TextView order_list, pet_name, total_price;
    private EditText note;
    private Button pay;
    private String username;

    //宠物护理医师
    private ArrayList<EmpDoctor> empDoctorList = new ArrayList<>();
    private MyPetServiceInterface listener;  // 用于回调接口

    public void setOnFloatingBallListener(MyPetServiceInterface listener) {
        this.listener = listener;  // 设置回调监听
    }

    public void showDialog(Context context, MyPet myPet, List<ServiceInfo> serviceInfoList) {
        // 创建一个AlertDialog.Builder对象
        // 创建BottomSheetDialog对象
        dialog = new BottomSheetDialog(context);

        // 获取数据
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("loggedInUsername", null);


        // 获取布局解析器
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.apponitment_dialog, null);

        // 初始化dialog内控件
        pet_image = dialogView.findViewById(R.id.pet_image);
        order_list = dialogView.findViewById(R.id.order_list);
        pet_name = dialogView.findViewById(R.id.pet_name);
        pay = dialogView.findViewById(R.id.pay);
        total_price = dialogView.findViewById(R.id.total_price);
        note = dialogView.findViewById(R.id.note);
        note.setText("");

        appointment_recycler = dialogView.findViewById(R.id.appointment_recycler);

        setText(context, myPet, serviceInfoList);

        setNetResponse(context);
        postNetRequest(context);


        // 设置关闭监听器
        dialog.setOnDismissListener(dialogInterface -> {
            if (listener != null){
                listener.openFloatingBall();
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String day = appointmentAdapter.getOrderInfo(0);
                String doctorId = appointmentAdapter.getOrderInfo(1);
                String timeslotId = appointmentAdapter.getOrderInfo(2);
                if (StringUtils.isEmpty(doctorId)){
                    Toast.makeText(context, "请选择医师", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.isEmpty(day)){
                    Toast.makeText(context, "请选择日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.isEmpty(timeslotId)){
                    Toast.makeText(context, "请选择时段", Toast.LENGTH_SHORT).show();
                    return;
                }




                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("order_type", 1);
                    jsonObject.put("username", username);
                    jsonObject.put("pickMethod", 2);
                    jsonObject.put("totalAmount", total_price.getText());
                    jsonObject.put("paymentMethod", 0);
                    jsonObject.put("appointmentPet", myPet.getId());
                    jsonObject.put("appointmentTime", day);
                    jsonObject.put("appointmentDoctor", doctorId);
                    jsonObject.put("timeslot_id", timeslotId);

                    // 创建 JSONArray 来存储 orderInfoList
                    JSONArray orderInfoArray = new JSONArray();
                    for (ServiceInfo serviceInfo : serviceInfoList) {
                        JSONObject orderInfoJson = new JSONObject();
                        orderInfoJson.put("shopping_id", serviceInfo.getId());
                        orderInfoJson.put("name", serviceInfo.getName());          // name
                        orderInfoJson.put("price", serviceInfo.getPrice());        // price
                        orderInfoJson.put("quantity", 1);  // quantity
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

    public void initAdapter(Context context) {
        // 设置适配器
        appointmentAdapter = new AppointmentAdapter(context, empDoctorList);
        appointment_recycler.setAdapter(appointmentAdapter);
        appointment_recycler.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setText(Context context, MyPet myPet, List<ServiceInfo> serviceInfoList) {
        Glide.with(context)
                .load(myPet.getPetImagePath())
                .into(pet_image);
        pet_name.setText(myPet.getPetName());

        String order = "";
        BigDecimal totalPrice = BigDecimal.ZERO; // 初始化为 0

        for (ServiceInfo serviceInfo : serviceInfoList) {
            order = (order.equals("") ? serviceInfo.getName() : order + " " + serviceInfo.getName());

            // 使用 BigDecimal 累加价格
            totalPrice = totalPrice.add(BigDecimal.valueOf(serviceInfo.getPrice()));
        }

        order_list.setText(order);

        // 设置总价，保留两位小数
        total_price.setText(String.valueOf(totalPrice));
    }

    //发送网络请求
    private void postNetRequest(Context context){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("job", 1);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        String jsonPayload = jsonObject.toString();
        netRequest.postEmpDoctorRequest("queryAllEmpByJob", jsonPayload);
    }

    //接受网络请求返回数据
    private void setNetResponse(Context context){
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("postEmpDoctorRequest")) {
                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);

                    // 遍历 JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject json = jsonResponse.getJSONObject(i);
                        EmpDoctor empDoctor = new EmpDoctor(
                                json.getInt("id"),
                                json.getString("phone"),
                                json.getString("name"),
                                json.getInt("gender"),
                                json.getString("image"),
                                json.getInt("job"));
                        System.out.println("empDoctor: " + empDoctor);
                        empDoctorList.add(empDoctor);
                    }
                    initAdapter(context);
                }

                if (requestType.equals("postOrderRequest")) {
                    Toast.makeText(context, "订单支付成功!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    if (listener != null){
                        listener.navigateToOrder();
                    }
                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postEmpDoctorRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }

                if (requestType.equals("postOrderRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}