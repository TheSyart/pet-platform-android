package com.example.petstore.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petstore.Enum.OrderStatus;
import com.example.petstore.R;
import com.example.petstore.activity.ShoppingActivity;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.pojo.Address;
import com.example.petstore.pojo.Order;
import com.example.petstore.pojo.OrderInfo;
import com.example.petstore.utils.DialogUtils;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.utils.RecentWeekDates;
import com.example.petstore.utils.StringUtils;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private NetRequest netRequest;
    private Boolean[] count;
    private Context context;
    private DialogUtils dialogUtils = new DialogUtils();
    private ArrayList<Order> orderArrayList;
    private ArrayList<Address> addressList;
    private ArrayList<List<OrderInfo>> orderInfoList;
    private String type = "";
    private UpdateOrderClickListener updateOrderClickListener;
    private String id;

    // 定义接口
    public interface UpdateOrderClickListener {
        void reloadOrder();
    }

    // 适配器的构造方法
    public OrderAdapter(Context context,
                        ArrayList<Order> orderArrayList,
                        ArrayList<Address> addressList,
                        ArrayList<List<OrderInfo>> orderInfoList,
                        UpdateOrderClickListener updateOrderClickListener) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        this.addressList = addressList;
        this.orderInfoList = orderInfoList;
        this.updateOrderClickListener = updateOrderClickListener;

        if (orderArrayList != null && !orderArrayList.isEmpty() && count == null){
            initCount();
        }
        setNetResponse(context);
        // 获取数据
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("loggedInId", null);
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {

        Order order = orderArrayList.get(position);
        List<OrderInfo> orderInfo = orderInfoList.get(position);
        Address address = addressList.get(position);

        if (type.equals("more")){
            moreCount();
            type = "";
        }else if (type.equals("refresh")){
            initCount();
            type = "";
        }

        //判断下拉是否打开
        if (count[position]){
            holder.noteList.setVisibility(View.VISIBLE);

            //判断取货方式
            holder.postList.setVisibility(order.getPickMethod() == 0 ? View.VISIBLE : View.GONE);
            holder.reserveList.setVisibility(order.getPickMethod() == 1 ? View.VISIBLE : View.GONE);
            holder.appointmentList.setVisibility(order.getPickMethod() == 2 ? View.VISIBLE : View.GONE);

        }else {
            holder.postList.setVisibility(View.GONE);
            holder.reserveList.setVisibility(View.GONE);
            holder.noteList.setVisibility(View.GONE);
            holder.appointmentList.setVisibility(View.GONE);
        }

        // 更新箭头图标和文本
        holder.pullOrPush.setImageResource(count[position] ?
                R.drawable.baseline_keyboard_arrow_up_24 : R.drawable.baseline_keyboard_arrow_down_24);
        holder.pullOrPush_word.setText(count[position] ? "向上拉起" : "查看更多");

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 切换状态
                count[position] = !count[position];

                // 通知适配器刷新该条目
                notifyItemChanged(position);
            }
        });

        holder.secretKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUtils.showMessageDialog(context, "取货码" ,order.getSecretKey(), "温馨提示:请妥善保管取货码!");
            }
        });

        holder.pike_status.setText(
                order.getPickMethod() == 0 ? "送货上门" :
                        order.getPickMethod() == 1 ? "线下自提": "门店服务");
        holder.order_id.setText(order.getOrderId());
        holder.total_price.setText(String.valueOf(order.getTotalAmount()));
        holder.total_num.setText(String.valueOf(orderInfo.size()));

        holder.address_details.setText(
                        address.getAddressTitle() + " " +
                        address.getAddressDetails() + " " +
                        address.getDoor()
        );
        holder.people_details.setText(
                        address.getName() + " " +
                        (address.getGender() == 0 ? "先生" : "女士") + " " +
                        address.getPhone()
        );
        holder.pay_details.setText(
                        order.getPaymentMethod() == 0 ? "微信支付" :
                        order.getPaymentMethod() == 1 ? "支付宝" : "信用卡"
        );


//        0.订单待确认 1.商品待配送 2.商品自取中
//        3.服务待服务 4.商品配送中 5.商品已到货
//        6.订单取消中 7.服务已超时 8.申请退货中
//        9.订单已退款 10.订单已完成



        holder.order_status.setText(OrderStatus.fromCode(order.getOrderStatus()).getDescription());

        //订单超时提示文本
        holder.timeout_text.setVisibility(order.getOrderStatus() == 7 ? View.VISIBLE : View.INVISIBLE);


        //订单删除
        //订单已退款或订单已完成时，才能删除
        holder.delete_order.setVisibility(
                order.getOrderStatus() == 9
                || order.getOrderStatus() == 10 ? View.VISIBLE : View.GONE)
        ;
        holder.delete_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNetRequest(order.getOrderId(), null, 1);
            }
        });

        //订单申请退货
        //订单已完成或订单已到货才能申请退款
        holder.refund_order.setVisibility(
                order.getOrderStatus() == 5 ||
                        order.getOrderStatus() == 10 ? View.VISIBLE : View.INVISIBLE
        );
        holder.refund_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNetRequest(order.getOrderId(), 8, null);
            }
        });


        //订单取消
        //订单不在5,6,8,9,10，才能取消，退钱
        holder.remove_order.setVisibility(
                order.getOrderStatus() != 5 &&
                        order.getOrderStatus() != 6 &&
                            order.getOrderStatus() != 8 &&
                                order.getOrderStatus() != 9 &&
                                    order.getOrderStatus() != 10 ? View.VISIBLE : View.GONE);
        holder.remove_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNetRequest(order.getOrderId(), 6, null);
            }
        });

        //订单确认收货
        //订单在2,3,4,5，才能确认收货
        holder.finish_order.setVisibility(
                order.getOrderStatus() == 2 ||
                        order.getOrderStatus() == 3 ||
                            order.getOrderStatus() == 4 ||
                                order.getOrderStatus() == 5 ? View.VISIBLE : View.GONE
        );
        holder.finish_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNetRequest(order.getOrderId(), 10, null);
            }
        });

        holder.createdata.setText(order.getCreateDate());
        holder.reserve_details.setText((order.getReservedName() + " " + order.getReservedPhone()));
        holder.note.setText(StringUtils.isEmpty(order.getNote()) ? "无" : order.getNote());

        holder.doctor_details.setText(order.getDoctorName() + " " + order.getDoctorPhone());
        holder.pet_name.setText(order.getPetName());
        holder.timeslot.setText(order.getAppointmentTime() + " " + order.getServiceTimeSlot());

        // 设置横向布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.orderSecondary_recycler.setLayoutManager(layoutManager);

        // 设置适配器
        SecondaryOrderAdapter secondaryAdapter = new SecondaryOrderAdapter(context, orderInfo);
        holder.orderSecondary_recycler.setAdapter(secondaryAdapter);

    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public void setType(String passType){
        type = passType;
    }

    public void moreCount(){
         if (count.length < orderArrayList.size()){
            Boolean[] newCount = new Boolean[orderArrayList.size()]; // 初始化 新count 数组
            //传递老的值
            for (int i = 0; i < count.length; i++) {
                newCount[i] = count[i];
            }
            //增加新的值
            for (int i = count.length - 1; i < orderArrayList.size(); i++) {
                newCount[i] = false;
            }
            count = newCount;
        }
    }

    public void initCount(){
        // 初始化 count 数组
        count = new Boolean[orderArrayList.size()];
        for (int i = 0; i < orderArrayList.size(); i++) {
            count[i] = false; // 初始化为 false
        }
    }
    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView order_id, total_price, total_num, address_details ,pay_details, people_details,
                pullOrPush_word, order_status, secretKey, reserve_details, createdata, note,
                pike_status, doctor_details, pet_name, timeslot, timeout_text;
        RecyclerView orderSecondary_recycler;
        ImageView pullOrPush;
        View more, postList, reserveList, noteList, appointmentList;
        Button finish_order, refund_order, remove_order, delete_order;
        public OrderHolder(View itemView) {
            super(itemView);
            order_id = itemView.findViewById(R.id.order_id);
            total_price = itemView.findViewById(R.id.total_price);
            total_num = itemView.findViewById(R.id.total_num);
            address_details = itemView.findViewById(R.id.address_details);
            people_details = itemView.findViewById(R.id.people_details);
            pay_details = itemView.findViewById(R.id.pay_details);
            orderSecondary_recycler = itemView.findViewById(R.id.orderSecondary_recycler);
            pullOrPush_word = itemView.findViewById(R.id.pullOrPush_word);
            pullOrPush = itemView.findViewById(R.id.pullOrPush);
            order_status = itemView.findViewById(R.id.order_status);
            secretKey = itemView.findViewById(R.id.secretKey);
            createdata = itemView.findViewById(R.id.createdata);
            note = itemView.findViewById(R.id.note);
            pike_status = itemView.findViewById(R.id.pike_status);


            more = itemView.findViewById(R.id.more);
            postList = itemView.findViewById(R.id.postList);
            reserve_details = itemView.findViewById(R.id.reserve_details);
            reserveList = itemView.findViewById(R.id.reserveList);
            noteList = itemView.findViewById(R.id.noteList);

            appointmentList = itemView.findViewById(R.id.appointmentList);
            doctor_details = itemView.findViewById(R.id.doctor_details);
            pet_name = itemView.findViewById(R.id.pet_name);
            timeslot = itemView.findViewById(R.id.timeslot);

            timeout_text = itemView.findViewById(R.id.timeout_text);
            finish_order = itemView.findViewById(R.id.finish_order);
            remove_order = itemView.findViewById(R.id.remove_order);
            refund_order = itemView.findViewById(R.id.refund_order);
            delete_order = itemView.findViewById(R.id.delete_order);



        }
    }

    //发送网络请求
    private void postNetRequest(String orderId, Integer orderStatus, Integer status){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("operatorType", 0);
            jsonObject.put("order_id", orderId);
            jsonObject.put("orderStatus", orderStatus);
            jsonObject.put("status", status);
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        String jsonPayload = jsonObject.toString();
        netRequest.postOrderRequest("updateOrderStatus", jsonPayload);
    }

    //接受网络请求返回数据
    private void setNetResponse(Context context) {
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("postOrderRequest")) {
                    // 触发回调
                    if (updateOrderClickListener != null) {
                        updateOrderClickListener.reloadOrder();
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postOrderRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
