package com.example.petstore.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.petstore.Adapter.DynamicsAdapter;
import com.example.petstore.Adapter.ImageAdapter;
import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.dialogUtils.ShowCalendarDialog;
import com.example.petstore.dialogUtils.ShowAddMyPetDialog;
import com.example.petstore.interfaces.AddPetInterface;
import com.example.petstore.pojo.Dynamics;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.dialogUtils.ShowDynamicsDialog;
import com.example.petstore.utils.LoadPicture;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import java.util.ArrayList;


public class HomeActivity extends Fragment implements AddPetInterface, View.OnClickListener {
    //用户宠物轮播图
    private TabLayout tabLayout;
    private ImageAdapter imageAdapter;
    private ViewPager2 viewPager;

    private String username;
    private String name;
    private RecyclerView recyclerView;
    private DynamicsAdapter dynamicsAdapter;

    //存储从服务器获取的数据
    //动态数据
    private ArrayList<Dynamics> dynamicsArrayList = new ArrayList<>();

    //我的宠物数据
    private ArrayList<String> pet_idList = new ArrayList<>();
    private ArrayList<String> pet_nameList = new ArrayList<>();
    private ArrayList<String> pet_breedList = new ArrayList<>();
    private ArrayList<String> pet_sexList = new ArrayList<>();
    private ArrayList<String> pet_weightList = new ArrayList<>();
    private ArrayList<String> pet_birthList = new ArrayList<>();
    private ArrayList<String> pet_coatList = new ArrayList<>();
    private ArrayList<String> pet_imageList = new ArrayList<>();
    private ArrayList<String> pet_detailsList = new ArrayList<>();
    private ArrayList<String> parent_idList = new ArrayList<>();

    private ImageView send;
    private ImageView petEncyclopedia;
    private ImageView pet_calendar;
    private ImageView myPet;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private byte[] imageBytes;
    private ShowDynamicsDialog showDynamicsDialog = new ShowDynamicsDialog();
    private ShowAddMyPetDialog showMyPetDialog = new ShowAddMyPetDialog();
    private SmartRefreshLayout smartRefreshLayout;
    private NetRequest netRequest;
    private int dynamicsNum = 0;
    private int photoStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_activity, container, false);

        init(view);

        return view;
    }
    public void init(View view) {
        //网络请求返回数据
        setNetResponse();

        //宠物头像轮播图
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        // 设置适配器
        
        imageAdapter = new ImageAdapter(getActivity(), pet_imageList, pet_nameList);
        viewPager.setAdapter(imageAdapter);
        // 设置 TabLayout
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText("")
        ).attach();



        myPet = view.findViewById(R.id.myPet);
        pet_calendar = view.findViewById(R.id.pet_calendar);
        send = view.findViewById(R.id.send);
        petEncyclopedia = view.findViewById(R.id.petEncyclopedia);
        myPet.setOnClickListener(this);
        pet_calendar.setOnClickListener(this);
        send.setOnClickListener(this);
        petEncyclopedia.setOnClickListener(this);

        // 获取数据
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("loggedInUsername", null);
        name = sharedPreferences.getString("loggedInName", null);

        //获取动态数据
        netRequest.getDynamicsRequest("getAllInformation", String.valueOf(dynamicsNum), username);

        //获取我的宠物数据
        String jsonPayload = "{\"username\":\"" + username + "\"}";
        netRequest.postMyPetRequest("getMyPet", jsonPayload);


        recyclerView = view.findViewById(R.id.dynamics_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 确保设置了适配器
        dynamicsAdapter = new DynamicsAdapter(getActivity(), dynamicsArrayList, username);
        recyclerView.setAdapter(dynamicsAdapter);

        // 初始化 ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        LoadPicture loadPicture = new LoadPicture();
                        loadPicture.onActivityResult(result.getResultCode(), data, getActivity(), "Images");

                        if (photoStatus == 2){
                            // 获取选择的图片的字节数组
                            imageBytes = loadPicture.getImageBytes();
                            System.out.println("上载图片：" + imageBytes);
                            //展示选择图片
                            showDynamicsDialog.setImage(imageBytes, getActivity());
                        }else if (photoStatus == 1){
                            showMyPetDialog.getImage(loadPicture.getImageBytes(), getActivity());
                        }


                    }
                });

        //初始化下拉刷新组件
        smartRefreshLayout = view.findViewById(R.id.smartRefresh);

        // 设置加载更多的监听器
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            System.out.println(" 当前动态数量： " + dynamicsNum);
            netRequest.getDynamicsRequest("getAllInformation", String.valueOf(dynamicsNum), username);
            // 完成加载更多，第一个参数是延迟时间，第二个参数是是否刷新成功，第三个参数是是否还有更多数据
            smartRefreshLayout.finishLoadMore(500, true, false); // 假设没有更多数据了
        });

        // 设置刷新的监听器
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            // 刷新操作
            reload();
            System.out.println(" 当前动态数量： " + dynamicsNum);
            // 完成刷新，第一个参数是延迟时间，第二个参数是是否刷新成功，第三个参数是是否还有更多数据
            smartRefreshLayout.finishRefresh(500, true, false); // 假设没有更多数据了
        });
    }

    @Override
    public void onClick(View view) {

        //生成dialog，添加我的宠物
        if (view.getId() == R.id.myPet){
            photoStatus = 1;
            showMyPetDialog.setOnReloadable(HomeActivity.this);  // 设置回调
            showMyPetDialog.showDialog(getActivity(), pickImageLauncher);
        }

        //生成dialog，编辑发布动态
        if (view.getId() == R.id.send){
            photoStatus = 2;
            showDynamicsDialog.showDialog(HomeActivity.this, getActivity(), pickImageLauncher);
        }

        //打开宠物百科页面
        if (view.getId() == R.id.petEncyclopedia){
            Intent intent = new Intent(getActivity(), EncyclopediaActivity.class);
            startActivity(intent);
        }

        //打开宠物日历对话框
        if (view.getId() == R.id.pet_calendar){
            ShowCalendarDialog showCalendarDialog = new ShowCalendarDialog();
            showCalendarDialog.showDialog(getActivity());
        }
    }

    //接受网络请求返回数据
    private void setNetResponse() {
        netRequest = new NetRequest(new JwtUtils(), getContext(), new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                //动态数据
                if (requestType.equals("getDynamicsRequest")) {
                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);


                    // 遍历 JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {

                        JSONObject jsonDynamic = jsonResponse.getJSONObject(i);

                        // 使用 optString 和 optInt 获取字段，防止字段值为 null 导致崩溃
                        Dynamics dynamics = new Dynamics(
                                jsonDynamic.optString("id", ""),
                                jsonDynamic.optString("username", ""),
                                jsonDynamic.optString("name", ""),
                                jsonDynamic.optString("sendtime", ""),
                                jsonDynamic.optString("content", ""),
                                jsonDynamic.optString("image", ""),
                                jsonDynamic.optString("image_path", ""),
                                jsonDynamic.optInt("likeCount", 0),
                                jsonDynamic.optBoolean("isLike", false)
                        );

                        dynamicsArrayList.add(dynamics);
                        dynamicsNum++;
                        System.out.println("当前动态数量: " + dynamicsNum);
                    }

                    dynamicsAdapter.notifyDataSetChanged();

                }

                //我的宠物数据
                if (requestType.equals("postMyPetRequest")){
                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);

                    // 遍历 JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject json = jsonResponse.getJSONObject(i);
                        pet_idList.add(json.getString("id"));
                        pet_nameList.add(json.getString("pet_name"));
                        pet_breedList.add(json.getString("pet_breed"));
                        pet_sexList.add(json.getString("pet_sex"));
                        pet_weightList.add(json.getString("pet_weight"));
                        pet_birthList.add(json.getString("pet_birth"));
                        pet_coatList.add(json.getString("pet_coat"));
                        pet_detailsList.add(json.getString("pet_details"));
                        parent_idList.add(json.getString("parent_id"));
                        pet_imageList.add(json.getString("image"));

                    }
                    imageAdapter.notifyDataSetChanged();
                }

            }
            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("getDynamicsRequest")) {
                    Toast.makeText(getActivity(), "动态获取失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("postMyPetRequest")){
                    Toast.makeText(getActivity(), "新增宠物失败: " + errorMessage, Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


    //刷新动态
    public void reload() {
        // 清空所有数据
        dynamicsArrayList.clear();

        // 重置动态计数
        dynamicsNum = 0;

        // 通知适配器数据已清空
        dynamicsAdapter.notifyDataSetChanged();

        // 重新请求获取动态数据
        netRequest.getDynamicsRequest("getAllInformation", String.valueOf(dynamicsNum), username);
    }

    //刷新宠物
    public void reloadPet() {
        // 清空所有宠物相关的数据
        pet_idList.clear();
        pet_nameList.clear();
        pet_breedList.clear();
        pet_sexList.clear();
        pet_weightList.clear();
        pet_birthList.clear();
        pet_coatList.clear();
        pet_imageList.clear();
        pet_detailsList.clear();
        parent_idList.clear();

        // 通知适配器数据已清空
        imageAdapter.notifyDataSetChanged();

        // 重新请求获取宠物数据
        String jsonPayload = "{\"username\":\"" + username + "\"}"; // 获取用户名以便请求
        netRequest.postMyPetRequest("getMyPet", jsonPayload);
    }


}
