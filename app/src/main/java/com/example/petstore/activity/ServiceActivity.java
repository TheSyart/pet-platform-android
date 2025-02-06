package com.example.petstore.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petstore.Adapter.ServiceAdapter;
import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.dialogUtils.ShowAddMyPetDialog;
import com.example.petstore.dialogUtils.ShowAppointmentDialog;
import com.example.petstore.dialogUtils.ShowMyPetDialog;
import com.example.petstore.interfaces.AddPetInterface;
import com.example.petstore.interfaces.FragmentInteractionListener;
import com.example.petstore.interfaces.MyPetServiceInterface;
import com.example.petstore.pojo.MyPet;
import com.example.petstore.pojo.ServiceInfo;
import com.example.petstore.pojo.ServiceType;
import com.example.petstore.utils.CircleMenuLayout;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.utils.LoadPicture;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceActivity extends Fragment implements MyPetServiceInterface, AddPetInterface {
    private ShowMyPetDialog showMyPetDialog = new ShowMyPetDialog();
    private ShowAddMyPetDialog showAddMyPetDialog = new ShowAddMyPetDialog();
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private String username;
    private NetRequest netRequest;
    private CircleMenuLayout mCircleMenuLayout;
    private CardView card;
    private ImageView back, pet_image, pet_more;
    private TextView pet_name;
    private ServiceAdapter serviceAdapter;
    private RecyclerView service_recycler;
    private String[] mItemTexts;
    private String[] mItemImgs;
    private ArrayList<MyPet> myPetList = new ArrayList<>();
    private ArrayList<ServiceType> serviceTypes = new ArrayList<>();
    private ArrayList<ServiceInfo> currentServiceInfos = new ArrayList<>();
    private Map<Integer, ArrayList<ServiceInfo>> serviceInfosMap = new HashMap<>();
    private Map<Integer, Boolean[]> countMap = new HashMap<>();
    private MyPet currentPet;
    private FragmentInteractionListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            listener = (FragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement FragmentInteractionListener");
        }
    }

    // 调用此方法以切换 Fragment
    public void switchToOrder() {
        if (listener != null) {
            listener.onFragmentInteraction(new OrderActivity());
        }
    }

    @Override
    public void onPetSelected(MyPet myPet) {
        setPet(myPet);
    }

    @Override
    public void startAddMyPetDialog() {
        showAddMyPetDialog.setOnReloadable(ServiceActivity.this);  // 设置回调
        showAddMyPetDialog.showDialog(getActivity(), pickImageLauncher);
    }

    @Override
    public void openFloatingBall() {
        showFloatingBall();
    }

    @Override
    public void navigateToOrder() {
        switchToOrder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_activity, container, false);

        // 先初始化布局和网络请求
        init(getActivity(), view);

        // 显示悬浮窗
        showFloatingBall();

        //接受返回数据
        setNetResponse();

        // 执行网络请求
        postNetResponse();

        return view;
    }

    public void init(Context context, View view) {

        // 初始化 ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        LoadPicture loadPicture = new LoadPicture();
                        loadPicture.onActivityResult(result.getResultCode(), data, getActivity(), "Images");

                        showAddMyPetDialog.getImage(loadPicture.getImageBytes(), getActivity());

                    }
                });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("loggedInUsername", null);

        pet_image = view.findViewById(R.id.pet_image);
        pet_name = view.findViewById(R.id.pet_name);
        pet_more = view.findViewById(R.id.pet_more);

        card = view.findViewById(R.id.card);
        back = view.findViewById(R.id.back);
        service_recycler = view.findViewById(R.id.service_recycler);

        back.setVisibility(View.INVISIBLE);


        // 获取屏幕宽度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;  // 获取屏幕宽度

        // 设置卡片为屏幕宽度的90%（可根据需要调整）
        int cardSize = (int) (screenWidth * 1);

        // 设置卡片的宽度和高度为 cardSize
        ViewGroup.LayoutParams params = card.getLayoutParams();
        params.width = cardSize;
        params.height = cardSize;
        card.setLayoutParams(params);

        // 设置圆角半径为宽度的一半，使其成为圆形
        card.setRadius(cardSize / 2.0f);

        // 初始化 CircleMenuLayout
        mCircleMenuLayout = view.findViewById(R.id.menulayout);

        mCircleMenuLayout.setOnMenuItemClickListener(new CircleMenuLayout.OnMenuItemClickListener() {

            @Override
            public void itemClick(View view, int pos) {
                back.setVisibility(View.VISIBLE);
                card.setVisibility(View.GONE);
                service_recycler.setVisibility(View.VISIBLE);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", serviceTypes.get(pos).getTypeId());
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                // 将最终 JSON 对象转换为字符串
                String jsonPayload = jsonObject.toString();
                netRequest.postServiceInfoRequest("queryPetServiceById", jsonPayload);
            }

            @Override
            public void itemCenterClick(View view) {
                Toast.makeText(context,
                        "you can do something just like ccb  ",
                        Toast.LENGTH_SHORT).show();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.setVisibility(View.VISIBLE);
                service_recycler.setVisibility(View.GONE);
                back.setVisibility(View.INVISIBLE);
                //清空上个类别数据
                serviceAdapter.refreshData(new ArrayList<ServiceInfo>());
            }
        });

        pet_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showMyPetDialog.setOnPetSelectedListener(ServiceActivity.this);  // 设置回调
                showMyPetDialog.showDialog(getActivity(), myPetList);
            }
        });
    }

    public void initAdapter(Context context) {
        // 设置适配器
        serviceAdapter = new ServiceAdapter(getContext(), currentServiceInfos, this);
        service_recycler.setAdapter(serviceAdapter);
        service_recycler.setLayoutManager(new LinearLayoutManager(context));
    }

    private void postNetResponse() {
        //获取我的宠物数据
        String jsonPayload = "{\"username\":\"" + username + "\"}";
        netRequest.postMyPetRequest("getMyPet", jsonPayload);
        netRequest.postServiceTypeRequest("queryAllPetServiceType", "");
    }

    private void setNetResponse() {
        netRequest = new NetRequest(new JwtUtils(), getContext(), new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("postServiceTypeRequest")) {
                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);
                    mItemTexts = new String[jsonResponse.length()];
                    mItemImgs = new String[jsonResponse.length()];

                    // 遍历 JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject json = jsonResponse.getJSONObject(i);
                        ServiceType serviceType = new ServiceType(
                                json.getInt("typeId"),
                                json.getString("name"),
                                json.getString("description"),
                                json.getString("image")
                        );
                        mItemTexts[i] = serviceType.getName();
                        mItemImgs[i] = serviceType.getImagePath();
                        serviceTypes.add(serviceType);
                    }

                    mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);
                }

                if (requestType.equals("postServiceInfoRequest")) {
                    //保存本次请求数据
                    ArrayList<ServiceInfo> serviceInfos = new ArrayList<>();

                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);

                    // 遍历 JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject json = jsonResponse.getJSONObject(i);

                        // 创建 ServiceInfo 对象
                        ServiceInfo serviceInfo = new ServiceInfo(
                                json.getLong("id"),
                                json.getString("name"),
                                json.getString("description"),
                                json.getDouble("price"),
                                json.getInt("shoppingTypeId"),
                                json.getString("image"),
                                false
                        );

                        // 添加到服务信息列表
                        serviceInfos.add(serviceInfo);

                    }
                    compareServiceInfoList(serviceInfos);

                    if (serviceAdapter == null) {
                        initAdapter(getContext());
                    } else {
                        serviceAdapter.initCount(getCount(currentServiceInfos.get(0).getShoppingTypeId()));
                        serviceAdapter.refreshData(currentServiceInfos);
                    }
                }

                //我的宠物数据
                if (requestType.equals("postMyPetRequest")) {
                    myPetList.clear();
                    JSONArray jsonResponse = new JSONArray(data);
                    if (jsonResponse != null && jsonResponse.length() > 0) {
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            // 获取每一个 JSONObject
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);

                            // 使用 JSONObject 获取字段值
                            MyPet pet = new MyPet(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("pet_name"),
                                    jsonObject.getString("pet_breed"),
                                    jsonObject.getString("pet_sex"),
                                    jsonObject.getString("pet_weight"),
                                    jsonObject.getString("pet_birth"),
                                    jsonObject.getString("pet_coat"),
                                    jsonObject.getString("pet_details"),
                                    jsonObject.getString("parent_id"),
                                    jsonObject.getString("image")
                            );

                            // 将解析后的 MyPet 对象添加到列表中
                            myPetList.add(pet);
                        }
                        //展示设置首个宠物
                        setPet(myPetList.get(0));
                    }
                }
            }


            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postServiceTypeRequest")) {
                    Toast.makeText(getActivity(), "网络请求失败", Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("postServiceInfoRequest")) {
                    Toast.makeText(getActivity(), "网络请求失败", Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("postMyPetRequest")) {
                    Toast.makeText(getActivity(), "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setPet(MyPet myPet) {
        Glide.with(getContext())
                .load(myPet.getPetImagePath())
                .into(pet_image);

        pet_name.setText(myPet.getPetName());

        currentPet = myPet;
    }

    //比较新老服务项目数据，更新countMap
    private void compareServiceInfoList(ArrayList<ServiceInfo> serviceInfos) {
        Integer key = serviceInfos.get(0).getShoppingTypeId();
        //是否已请求过该类型服务数据
        if (serviceInfosMap.containsKey(key)) {
            //请求过，传递老ifPay
            ArrayList<ServiceInfo> oldServiceInfos = serviceInfosMap.get(key);
            for (ServiceInfo newInfo : serviceInfos) {
                for (ServiceInfo oldInfo : oldServiceInfos) {
                    if (newInfo.getId() == oldInfo.getId()) {
                        newInfo.setIfPay(oldInfo.getIfPay());
                    }
                }
            }
        }

        serviceInfosMap.put(key, serviceInfos);


        // 创建一个布尔数组
        Boolean[] counts = new Boolean[serviceInfos.size()];

        for (int i = 0; i < serviceInfos.size(); i++) {
            counts[i] = serviceInfos.get(i).getIfPay();
        }
        countMap.put(key, counts);

        currentServiceInfos = serviceInfos;
    }

    //根据类别，返回count
    private Boolean[] getCount(int shoppingTypeId) {
        // 检查是否存在对应类别
        if (countMap.containsKey(shoppingTypeId)) {
            Boolean[] counts = countMap.get(shoppingTypeId);
            if (counts != null) {

                outMap();

                return counts;
            } else {
                System.out.println("类别 " + shoppingTypeId + " 的布尔数组为空。");
            }
        } else {
            System.out.println("类别 " + shoppingTypeId + " 不存在。");
        }
        return null;
    }

    //更新countMap与serviceInfoMap
    public void updateMap(ArrayList<ServiceInfo> infos, int index, Boolean value) {
        int category = infos.get(0).getShoppingTypeId();

        // 判断 category 是否存在于 countMap 中
        if (countMap.containsKey(category)) {
            // 获取该类别对应的布尔数组
            Boolean[] values = countMap.get(category);

            // 判断索引是否有效
            if (index >= 0 && index < values.length) {
                // 更新指定索引处的值
                values[index] = value;
                System.out.println("类别: " + category + ", 索引: " + index + " 更新为: " + value);
                System.out.println("<-------------------------------->");
                outMap();

                serviceInfosMap.put(category, infos);
            } else {
                System.out.println("索引无效！");
            }
        } else {
            System.out.println("类别不存在！");
        }
    }


    public void outMap() {
        // 遍历 countMap 并输出所有值
        for (Map.Entry<Integer, Boolean[]> entry : countMap.entrySet()) {
            Integer category = entry.getKey();  // 类别名称
            Boolean[] values = entry.getValue();  // 对应的布尔数组

            // 输出类别名称
            System.out.println("类别：" + category);

            // 输出布尔数组的内容
            if (values != null) {
                System.out.print("布尔数组：");
                for (Boolean value : values) {
                    System.out.print(value + " ");
                }
                System.out.println(); // 换行
            } else {
                System.out.println("布尔数组为空！");
            }
        }
    }

    private List<ServiceInfo> getOrder() {
        List<ServiceInfo> serviceInfoList = new ArrayList<>();
        serviceInfosMap.forEach((key, value) -> {
            value.forEach(serviceInfo -> {
                if (serviceInfo.getIfPay()) {
                    serviceInfoList.add(serviceInfo);
                }
            });
        });

        return serviceInfoList;
    }

    private void showFloatingBall() {
        EasyFloat.with(getActivity())
                .setLayout(R.layout.layout_floating_ball, view -> {
                    ImageView floatIcon = view.findViewById(R.id.image_float);
                    floatIcon.setOnClickListener(v -> {
                        List<ServiceInfo> serviceInfoList = getOrder();
                        if (currentPet == null) {
                            Toast.makeText(getActivity(), "请添加宠物!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (serviceInfoList == null || serviceInfoList.size() == 0) {
                            Toast.makeText(getActivity(), "请添加商品!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ShowAppointmentDialog showAppointmentDialog = new ShowAppointmentDialog();
                        showAppointmentDialog.setOnFloatingBallListener(ServiceActivity.this);  // 设置回调
                        showAppointmentDialog.showDialog(getActivity(), currentPet, getOrder());
                        EasyFloat.dismiss(); // 关闭悬浮球

                    });
                })
                .setShowPattern(ShowPattern.ALL_TIME) // 悬浮球显示模式
                .setSidePattern(SidePattern.RESULT_SIDE) // 吸附到屏幕边缘
                .setDragEnable(true) // 允许拖动
                .setGravity(Gravity.CENTER | Gravity.RIGHT) // 设置小球位置为屏幕下方
                .show();
    }


    @Override
    public void onPause() {
        super.onPause();
//        EasyFloat.dismiss(); // 关闭悬浮球
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyFloat.dismiss(); // 关闭悬浮球
    }

    @Override
    public void reloadPet() {
        Toast.makeText(getContext(), "nani", Toast.LENGTH_SHORT).show();
        String jsonPayload = "{\"username\":\"" + username + "\"}";
        netRequest.postMyPetRequest("getMyPet", jsonPayload);
        showMyPetDialog.dismissDialog();
    }
}
