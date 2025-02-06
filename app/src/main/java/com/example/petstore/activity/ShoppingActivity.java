package com.example.petstore.activity;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petstore.Adapter.CommodityAdapter;
import com.example.petstore.Adapter.NavAdapter;
import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.dialogUtils.ShowPayDialog;
import com.example.petstore.interfaces.FragmentInteractionListener;
import com.example.petstore.pojo.Commodity;
import com.example.petstore.pojo.OrderInfo;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.utils.StickyHeaderDecoration;
import com.example.petstore.utils.TopSmoothScroller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingActivity extends Fragment implements View.OnClickListener{
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private NavAdapter navAdapter;
    private CommodityAdapter commodityAdapter;
    private RecyclerView nav_recycler;
    private RecyclerView commodity_recycler;
    private TextView showPrice;
    private TextView cart_num;
    private Button pay;
    private ImageView cart;
    private NetRequest netRequest;
    private String username;

    //商品导航栏
    private ArrayList<String> navList = new ArrayList<>();

    //商品具体信息
    private ArrayList<Commodity> commodityList = new ArrayList<>();

    private Map<String, List<Commodity>> categorizedCommodities = new HashMap<>();
    private List<OrderInfo> orderInfoList = new ArrayList<>();
    private ShowPayDialog showPayDialog = new ShowPayDialog();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shopping_activity, container, false);
        setNetResponse();
        netRequest.postShoppingRequest("noPageQueryAllShopping", "");

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String resultValue = data.getStringExtra("type");
                            // 使用返回的数据 resultValue
                            if (resultValue.equals("add")){
                                showPayDialog.dismiss();
                                ShowPayDialog newShow= new ShowPayDialog();
                                newShow.showDialog(activityResultLauncher, this, getContext(), username, orderInfoList);
                            }
                        }
                    }
                }
        );
        init(getActivity(), view);

        return view;
    }

    public void init(Context context, View view) {
        // 获取数据
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("loggedInUsername", null);

        cart = view.findViewById(R.id.cart);
        cart.setOnClickListener(this);

        pay = view.findViewById(R.id.pay);
        pay.setOnClickListener(this);

        cart_num = view.findViewById(R.id.cart_num);
        showPrice = view.findViewById(R.id.total_price);
        nav_recycler = view.findViewById(R.id.nav_recycler);
        commodity_recycler = view.findViewById(R.id.commodity_recycler);

        // 设置适配器
        navAdapter = new NavAdapter(this, navList);
        nav_recycler.setAdapter(navAdapter);

        commodityAdapter = new CommodityAdapter(this, context, commodityList);
        commodity_recycler.setAdapter(commodityAdapter);
        commodity_recycler.addItemDecoration(new StickyHeaderDecoration(context));

        nav_recycler.setLayoutManager(new LinearLayoutManager(context));
        commodity_recycler.setLayoutManager(new LinearLayoutManager(context));

        navAdapter.setNavOnClickItemListener(new NavAdapter.NavOnClickItemListener() {
            @Override
            public void onItemclick(int position) {
                navAdapter.setCurrentPosition(position);
            }
        });

        commodity_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 获取 LinearLayoutManager
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 获取第一个可见的 item 位置
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // 检查位置是否有效
                if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
                    // 获取第一个可见项的类别
                    String currentGroupName = commodityAdapter.getGroupName(firstVisibleItemPosition);

                    // 查找该类别在导航栏中的索引位置
                    int navPosition = navList.indexOf(currentGroupName);

                    // 设置导航栏高亮
                    if (navPosition != -1) {  // 确保类别存在于导航栏中
                        navAdapter.setCurrentPosition(navPosition);
                    }
                }
            }
        });
    }

    //接受网络请求返回数据
    private void setNetResponse(){
        netRequest = new NetRequest(new JwtUtils(), getContext(), new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("postShoppingRequest")) {
                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);

                    // 遍历 JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject json = jsonResponse.getJSONObject(i);
                        addCommodity(new Commodity(
                                json.getString("category"),
                                json.getString("name"),
                                json.getDouble("price"),
                                json.getString("image"),
                                json.getInt("stock"),
                                json.getInt("id"),
                                json.getString("description")));
                    }

                    // 通过分类存储商品
                    for (String category : categorizedCommodities.keySet()) {
                        System.out.println("Category: " + category);
                        navList.add(category);  // 添加类别
                        for (Commodity commodity : categorizedCommodities.get(category)) {
                            System.out.println("Commodity: " + commodity);
                            commodityList.add(commodity);
                        }
                    }

                    navAdapter.notifyDataSetChanged();
                    commodityAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postShoppingRequest")) {
                }
            }
        });
    }

    public void updateComponent(BigDecimal price, int num){
        if (num == 0 ){
            cart_num.setVisibility(View.INVISIBLE);
        }else {
            cart_num.setVisibility(View.VISIBLE);
        }
        cart_num.setText(String.valueOf(num));
        showPrice.setText(String.valueOf(price));
    }
    private void addCommodity(Commodity commodity) {
        categorizedCommodities
                .computeIfAbsent(commodity.getCategory(), k -> new ArrayList<>())
                .add(commodity);
    }

    public void scrollToCategory(int position) {
        System.out.println(" 点击位置 : " + position);
        // 获取目录名称
        String category = navList.get(position);
        System.out.println(" 点击位置的导航名 : " + category);
        // 查找组头部对应的位置
        int dataSize = 0;
        for (Commodity commodity : commodityList) {
            if (category.equals(commodity.getCategory())) {
                break;
            }
            dataSize++;
        }
        TopSmoothScroller smoothScroller = new TopSmoothScroller(getContext());
        smoothScroller.setTargetPosition(dataSize);
        commodity_recycler.getLayoutManager().startSmoothScroll(smoothScroller);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pay || v.getId() == R.id.cart){
            if (cart_num.getText().equals("") || cart_num.getText() == null || cart_num.getText().equals("0")){
                Toast.makeText(getContext(), "请添加商品!", Toast.LENGTH_SHORT).show();
                return;
            }
            orderInfoList.clear();
            int count [] = commodityAdapter.getCommodityCount();
            BigDecimal price;
            BigDecimal quantity;
            for (int i = 0; i < count.length; i++) {
                if (count[i] > 0 ){

                    price = BigDecimal.valueOf(commodityList.get(i).getPrice());
                    quantity = BigDecimal.valueOf(count[i]);

                    OrderInfo orderInfo = new OrderInfo(
                            commodityList.get(i).getId(),
                            commodityList.get(i).getName(),
                            count[i],
                            commodityList.get(i).getPrice(),
                            price.multiply(quantity),
                            commodityList.get(i).getImageUrl()
                    );
                    orderInfoList.add(orderInfo);
                }
            }
            showPayDialog.showDialog(activityResultLauncher, this, getContext(), username, orderInfoList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
