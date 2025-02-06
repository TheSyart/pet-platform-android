package com.example.petstore.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petstore.Adapter.OrderAdapter;
import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.pojo.Address;
import com.example.petstore.pojo.Order;
import com.example.petstore.pojo.OrderInfo;
import com.example.petstore.utils.JwtUtils;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends Fragment implements OrderAdapter.UpdateOrderClickListener {
    private RadioButton total, ing, timeout, remove, finish;
    private RecyclerView order_recycler;
    private OrderAdapter orderAdapter;
    private NetRequest netRequest;
    private String username;
    private RadioGroup type_radio, info_radio;
    //为 postRequest() 添加去抖动逻辑，避免短时间内多次调用：
    private boolean isRequesting = false;
    //订单类型
    private int order_type = 0;
    //订单状态
    private int orderPart = 0;
    //统计已发送到前端的订单数量
    private int num = 0;

    //拉动刷新组件
    private SmartRefreshLayout smartRefreshLayout;

    //接收数据
    private ArrayList<Address> addressList = new ArrayList<>();
    private ArrayList<Order> orderArrayList = new ArrayList<>();
    private ArrayList<List<OrderInfo>> orderInfoList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_activity, container, false);
        // 获取数据
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("loggedInUsername", null);
        setNetResponse(getContext());
        postRequest();
        init(view);

        return view;
    }

    public void init(View view) {
        total = view.findViewById(R.id.total);
        ing = view.findViewById(R.id.ing);
        remove = view.findViewById(R.id.remove);
        finish = view.findViewById(R.id.finish);
        timeout = view.findViewById(R.id.timeout);
        timeout.setVisibility(View.GONE);

        type_radio = view.findViewById(R.id.type_radio);

        // 设置默认选中的 RadioButton
        type_radio.check(R.id.pet_shopping);

        type_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                orderAdapter.setType("refresh");
                if (checkedId == R.id.pet_shopping) {
                    order_type = 0;
                    ing.setText("待收货");
                    timeout.setVisibility(View.GONE);
                } else if (checkedId == R.id.pet_service) {
                    order_type = 1;
                    ing.setText("待服务");
                    timeout.setVisibility(View.VISIBLE);
                }

                // 重新设置默认选中状态并触发数据刷新
                info_radio.clearCheck();
                info_radio.check(R.id.total);
                orderPart = 0; // 确保切换时状态统一为默认值
                reload(); // 强制刷新数据
            }
        });



        info_radio = view.findViewById(R.id.info_radio);

        // 设置默认选中的 RadioButton
        info_radio.check(R.id.total);

        info_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                orderAdapter.setType("refresh");
                int newPart = 0; // 默认值
                if (checkedId == R.id.total) {
                    newPart = 0;
                } else if (checkedId == R.id.ing) {
                    newPart = 1;
                } else if (checkedId == R.id.remove) {
                    newPart = 2;
                } else if (checkedId == R.id.finish) {
                    newPart = 3;
                } else if (checkedId == R.id.timeout) {
                    newPart = 4;
                }

                // 只有当状态改变时才触发 reload()
                if (orderPart != newPart) {
                    orderPart = newPart;
                    reload();
                }
            }
        });



        order_recycler = view.findViewById(R.id.order_recycler);
        //初始化下拉刷新组件
        smartRefreshLayout = view.findViewById(R.id.smartRefresh);

        // 设置加载更多的监听器
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            orderAdapter.setType("more");
            System.out.println(" 当前订单数量： " + num);
            postRequest();
            // 完成加载更多，第一个参数是延迟时间，第二个参数是是否刷新成功，第三个参数是是否还有更多数据
            smartRefreshLayout.finishLoadMore(500, true, false); // 假设没有更多数据了
        });

        // 设置刷新的监听器
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            orderAdapter.setType("refresh");
            // 刷新操作
            reload();
            System.out.println(" 当前订单数量： " + num);
            // 完成刷新，第一个参数是延迟时间，第二个参数是是否刷新成功，第三个参数是是否还有更多数据
            smartRefreshLayout.finishRefresh(500, true, false); // 假设没有更多数据了
        });
    }

    public void initAdapter(Context context) {
        // 设置适配器
        orderAdapter = new OrderAdapter(
                getContext(),
                orderArrayList,
                addressList,
                orderInfoList,
                this
        );

        order_recycler.setAdapter(orderAdapter);
        order_recycler.setLayoutManager(new LinearLayoutManager(context));
    }

    private void postRequest() {
        if (isRequesting) return; // 忽略重复请求
        isRequesting = true;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_type", order_type);
            jsonObject.put("username", username);
            jsonObject.put("orderPart", orderPart);
            jsonObject.put("num", num);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        String jsonPayload = jsonObject.toString();
        netRequest.postOrderRequest("queryAllOrderByCustomerId", jsonPayload);

        // 延迟重置状态
        new android.os.Handler().postDelayed(() -> isRequesting = false, 500);
    }

    //接受网络请求返回数据
    private void setNetResponse(Context context) {
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("postOrderRequest")) {
                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);

                    // 遍历 JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject json = jsonResponse.getJSONObject(i);

                        //统计订单数量
                        num++;
                        System.out.println(" 当前动态数量: " + num);

                        // 订单信息
                        Order order = new Order(
                                json.getString("order_id"),
                                json.getString("customer_id"),
                                json.getInt("pickMethod"),
                                json.getDouble("totalAmount"),
                                json.getInt("paymentMethod"),
                                json.getString("note"),
                                json.getInt("orderstatus"),
                                json.getString("createdate"),
                                json.getString("reservedName"),
                                json.getString("reservedPhone"),
                                json.getString("secretKey"),
                                json.getString("appointmentTime"),
                                json.getString("doctorName"),
                                json.getString("doctorPhone"),
                                json.getString("petName"),
                                json.getString("serviceTimeSlot")
                        );
                        orderArrayList.add(order);


                        // 获取地址信息
                        // 获取 address 字段，并判断是否为 null
                        JSONObject addressJson = json.optJSONObject("address");
                        if (addressJson != null) {
                            Address address = new Address(
                                    null,
                                    null,
                                    addressJson.optString("name"),
                                    addressJson.optInt("gender"),
                                    addressJson.optString("phone"),
                                    addressJson.optString("addressTitle"),
                                    addressJson.optString("addressDetails"),
                                    addressJson.optString("door"),
                                    addressJson.optDouble("longitude"),
                                    addressJson.optDouble("latitude")
                            );
                            addressList.add(address);
                        } else {
                            // 处理 address 为空的情况
                            // 比如可以创建一个默认的 Address 对象，或者跳过
                            Address emptyAddress = new Address(
                                    null, null, "", 0, "", "", "", "", 0.0, 0.0
                            );
                            addressList.add(emptyAddress);
                        }




                        // 商品详情
                        JSONArray orderInfoArray = json.getJSONArray("orderInfoList");
                        List<OrderInfo> singleOrderInfoList = new ArrayList<>(); // 每个订单的商品列表

                        for (int j = 0; j < orderInfoArray.length(); j++) {
                            JSONObject orderInfoJson = orderInfoArray.getJSONObject(j);
                            OrderInfo orderInfo = new OrderInfo(
                                    null,
                                    orderInfoJson.getString("name"),
                                    orderInfoJson.getInt("quantity"),
                                    orderInfoJson.getDouble("price"),
                                    BigDecimal.valueOf(orderInfoJson.getDouble("subtotal")),
                                    orderInfoJson.getString("image")
                            );
                            singleOrderInfoList.add(orderInfo);
                        }

                        // 将每个订单的商品列表添加到嵌套列表中
                        orderInfoList.add(singleOrderInfoList);
                    }
                    if (orderAdapter == null){
                        initAdapter(context);
                    } else {
                        // 通知适配器数据更新
                        orderAdapter.notifyDataSetChanged();
                    }
                }

            }


            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postOrderRequest")) {
                    Toast.makeText(getContext(), "数据加载失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //刷新订单
    public void reload() {
        // 清空所有与订单相关的数据列表
        orderArrayList.clear();
        addressList.clear();
        orderInfoList.clear();

        // 重置订单计数
        num = 0;

        // 如果适配器已初始化，通知适配器数据已清空
        if (orderAdapter != null) {
            orderAdapter.notifyDataSetChanged();
        }

        // 重新请求获取订单数据
        postRequest();
    }


    @Override
    public void reloadOrder() {
        // 触发刷新
        smartRefreshLayout.autoRefresh();
    }
}

