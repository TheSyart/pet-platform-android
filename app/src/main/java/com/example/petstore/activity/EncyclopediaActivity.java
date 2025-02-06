package com.example.petstore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petstore.Adapter.DynamicsAdapter;
import com.example.petstore.Adapter.EncyclopediaAdapter;
import com.example.petstore.Adapter.FeedingAdapter;
import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.pojo.EncyclopediaSpecies;
import com.example.petstore.pojo.FeedingSkill;
import com.example.petstore.utils.JwtUtils;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class EncyclopediaActivity extends AppCompatActivity {
    private SmartRefreshLayout smartRefreshLayout;
    private TextView encyclopedia_title;
    private ImageView back;
    private ImageView back_home;
    private RecyclerView encyclopedia_recyclerview, feeding_recyclerview;
    private EncyclopediaAdapter encyclopediaAdapter;
    private FeedingAdapter feedingAdapter;
    private NetRequest netRequest;

    //宠物分类 以及分类下宠物 的图标 三元素数据
    private ArrayList<EncyclopediaSpecies> showSymbolList = new ArrayList<>();

    //宠物喂养技巧数据
    private ArrayList<FeedingSkill> feedingSkillArrayList =  new ArrayList<>();

    private int skillNum = 0;

    // 定义接口
    public interface OnItemClickListener {
        void onItemClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encyclopedia_activity);
        init();
    }

    public void init() {
        setNetResponse();

        //标题
        encyclopedia_title = findViewById(R.id.encyclopedia_title);

        //宠物类型列表
        encyclopedia_recyclerview = findViewById(R.id.encyclopedia_recyclerview);

        //宠物喂养技巧列表
        feeding_recyclerview = findViewById(R.id.feeding_recyclerview);
        // 初始化适配器
        feeding_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        feedingAdapter = new FeedingAdapter(EncyclopediaActivity.this, feedingSkillArrayList);
        feeding_recyclerview.setAdapter(feedingAdapter);



        //初始化返回home，可见
        back_home = findViewById(R.id.back_home);
        back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EncyclopediaActivity.this, PetPlatformActivity.class);
                startActivity(intent);
            }
        });

        //初始化返回上级按钮，不可见
        back = findViewById(R.id.back);
        back.setVisibility(View.GONE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back_home.setVisibility(View.VISIBLE);
                encyclopedia_title.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
                encyclopediaAdapter.updateType("species");
                encyclopediaAdapter.showBackHome();
            }
        });


        //初始化下拉刷新组件
        smartRefreshLayout = findViewById(R.id.smartRefresh);

        // 设置加载更多的监听器
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            System.out.println(" 当前喂养技巧数量： " + skillNum);
            netRequest.getPetFeedingSkillRequest("queryFeedingSkill", skillNum);
            // 完成加载更多，第一个参数是延迟时间，第二个参数是是否刷新成功，第三个参数是是否还有更多数据
            smartRefreshLayout.finishLoadMore(500, true, false); // 假设没有更多数据了
        });

        // 设置刷新的监听器
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            // 刷新操作
            reload();
            System.out.println(" 当前喂养技巧数量： " + skillNum);
            // 完成刷新，第一个参数是延迟时间，第二个参数是是否刷新成功，第三个参数是是否还有更多数据
            smartRefreshLayout.finishRefresh(500, true, false); // 假设没有更多数据了
        });

        postNetRequest();
    }
    private void postNetRequest() {
        netRequest.getEncyclopediaRequest("petSpecies", "");
        netRequest.getPetFeedingSkillRequest("queryFeedingSkill", skillNum);
    }

    //接受网络请求返回数据
    private void setNetResponse() {
        netRequest = new NetRequest(new JwtUtils(), this, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                //获取宠物百科分类图标与类名
                if (requestType.equals("getEncyclopediaRequest")) {
                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);
                    // 遍历 JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject json = jsonResponse.getJSONObject(i);
                        EncyclopediaSpecies encyclopediaSpecies =
                                new EncyclopediaSpecies(
                                        json.getString("id"),
                                        json.getString("name"),
                                        json.getString("image")
                                );
                        showSymbolList.add(encyclopediaSpecies);
                    }

                    // 初始化适配器
                    encyclopediaAdapter = new EncyclopediaAdapter(EncyclopediaActivity.this, showSymbolList, new OnItemClickListener() {
                        @Override
                        public void onItemClick() {
                            // 点击事件的处理逻辑
                            Toast.makeText(EncyclopediaActivity.this, "Item clicked!", Toast.LENGTH_SHORT).show();
                            back_home.setVisibility(View.GONE);
                            encyclopedia_title.setVisibility(View.INVISIBLE);
                            back.setVisibility(View.VISIBLE);

                        }
                    });
                    encyclopedia_recyclerview.setAdapter(encyclopediaAdapter);
                    // 设置 GridLayoutManager，每行4个item
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
                    encyclopedia_recyclerview.setLayoutManager(gridLayoutManager);
                }

                if (requestType.equals("getPetFeedingSkillRequest")) {
                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);
                    // 遍历 JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject json = jsonResponse.getJSONObject(i);
                        FeedingSkill feedingSkill = new FeedingSkill(
                                        json.getLong("id"),
                                        json.getString("title"),
                                        json.getString("content"),
                                        json.getString("image"),
                                        json.getString("createDate")
                                );
                        feedingSkillArrayList.add(feedingSkill);
                        skillNum++;
                        System.out.println("当前喂养技巧数量: " + skillNum);
                    }
                    feedingAdapter.notifyDataSetChanged();
                }

            }
            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("getEncyclopediaRequest")) {
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("getPetFeedingSkillRequest")) {

                }
            }
        });
    }

    //刷新宠物喂养技巧
    public void reload() {
        // 清空所有数据
        feedingSkillArrayList.clear();

        // 重置喂养技巧计数
        skillNum = 0;

        // 通知适配器数据已清空
        feedingAdapter.notifyDataSetChanged();

        // 重新请求获取喂养技巧数据
        netRequest.getPetFeedingSkillRequest("queryFeedingSkill", skillNum);
    }
}
