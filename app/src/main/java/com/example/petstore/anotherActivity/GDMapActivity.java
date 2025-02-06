package com.example.petstore.anotherActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.example.petstore.Adapter.PoiAdapter;
import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.pojo.Address;
import com.example.petstore.pojo.Bean;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.utils.MapUtils;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class GDMapActivity extends AppCompatActivity {
    private MapView mMapView;
    private AMap aMap;
    private AMapLocationClient mLocationClient;
    private PoiAdapter poiAdapter;
    private final List<Bean> beanList =new ArrayList<>();
    private Bean selectBean;
    private GeocodeSearch geocoderSearch;
    private boolean flag=false;//条目点击标志位
    private CardView addressSearch_card, address_card;
    private Button modify_address, save;
    private TextView address_title, address_details;
    private EditText search, phone, name, door;
    private ListView listView;
    private MapUtils mapUtils;
    private LinearLayoutCompat container_address;
    private RadioGroup radioGroup;
    private ImageView back;
    private NetRequest netRequest;

    //向后端录入时的数据
    private String username;
    private Integer selectGender;
    private Double longitude_pass;
    private Double latitude_pass;
    private Long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gd_map_activity);
        //隐私合规接口
        MapsInitializer.updatePrivacyShow(this,true,true);
        MapsInitializer.updatePrivacyAgree(this,true);

        // 获取数据
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("loggedInUsername", null);

        radioGroup = findViewById(R.id.gender_group);
        setNetResponse(getApplicationContext());

        listView = findViewById(R.id.listView);
        search = findViewById(R.id.et_search);

        // 初始化 MapView
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap(); // 获取 AMap 对象

        addressSearch_card = findViewById(R.id.addressSearch_card);
        address_card = findViewById(R.id.address_card);
        modify_address = findViewById(R.id.modify_address);
        address_title = findViewById(R.id.address_title);
        address_details = findViewById(R.id.address_details);
        container_address = findViewById(R.id.container_address);
        back = findViewById(R.id.back);

        phone = findViewById(R.id.phone);
        name = findViewById(R.id.name);
        door = findViewById(R.id.door);
        save = findViewById(R.id.save);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search.getVisibility() == View.VISIBLE){
                    setComponent();
                } else {
                    finish();
                }

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id",id);
                    jsonObject.put("username", username);
                    jsonObject.put("name", name.getText());
                    jsonObject.put("gender", selectGender);
                    jsonObject.put("phone", phone.getText());
                    jsonObject.put("addressTitle", address_title.getText());
                    jsonObject.put("addressDetails", address_details.getText());
                    jsonObject.put("longitude", longitude_pass);
                    jsonObject.put("latitude", latitude_pass);
                    jsonObject.put("door", door.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                // 将最终 JSON 对象转换为字符串
                String jsonPayload = jsonObject.toString();
                netRequest.postAddressRequest("updateAddress", jsonPayload);
            }
        });

        //收货人性别选择
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.man){
                    selectGender = 0;
                }else if (checkedId == R.id.women){
                    selectGender = 1;
                }
            }
        });

        // 读取从 ShoppingActivity 传递过来的数据
        Intent data = getIntent();
        getIntent(data);

        //获取地址时，展示一系列界面
        modify_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setVisibility(View.VISIBLE);
                addressSearch_card.setVisibility(View.VISIBLE);
                address_card.setVisibility(View.INVISIBLE);
                container_address.setVisibility(View.GONE);
            }
        });

        poiAdapter = new PoiAdapter(beanList,this, this);
        listView.setAdapter(poiAdapter);

        mapUtils = new MapUtils(this, getApplicationContext(), poiAdapter, aMap, beanList);
        mapUtils.permission();
        //地图移动监听
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }
            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                if (!flag){
                    if (poiAdapter != null){
                        poiAdapter.setSelectPosition(0);
                    }
                    try {
                        mapUtils.getGeocodeSearch(cameraPosition.target);
                        mapUtils.setMapCenter(cameraPosition.target);
                    } catch (AMapException e) {
                        throw new RuntimeException(e);
                    }
                    listView.smoothScrollToPosition(0);
                }
                flag = false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                poiAdapter.setSelectPosition(position);
                poiAdapter.notifyDataSetChanged();
                selectBean = beanList.get(position);
                flag = true;//当是点击地址条目触发的地图移动时，不进行逆地理解析
                aMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(selectBean.getLatitude(),selectBean.getLongitude()))); //设置地图中心点
            }
        });
        //搜索监听
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())){
                    mapUtils.showTip(s.toString(), beanList);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void setAddress(String addressName, String addressDetails, Double longitude, Double latitude){
        address_title.setText(addressName);
        address_details.setText(addressDetails);
        longitude_pass = longitude;
        latitude_pass = latitude;
    }

    public void getIntent(Intent data){
        if (data.getStringExtra("type").equals("add")){
            setComponent();
        }else if (data.getStringExtra("type").equals("modify")){
            Gson gson = new Gson();
            String addressJson = data.getStringExtra("address");
            Address address = gson.fromJson(addressJson, Address.class);
            address_title.setText(address.getAddressTitle());
            address_details.setText(address.getAddressDetails());
            door.setText(address.getDoor());
            name.setText(address.getName());
            phone.setText(address.getPhone());
            radioGroup.check(address.getGender() == 0 ? R.id.man : R.id.women);
            id = Long.valueOf(address.getId());
            longitude_pass = address.getLongitude();
            latitude_pass = address.getLatitude();
            setComponent();
        }

    }
    /**
     * 判断类型，设置界面
     **/
    public void setComponent(){
        search.setVisibility(View.INVISIBLE);
        addressSearch_card.setVisibility(View.INVISIBLE);
        address_card.setVisibility(View.VISIBLE);
        if (address_title.getText() != null && address_title.getText() != ""){
            modify_address.setText("修改地址");
            container_address.setVisibility(View.VISIBLE);
        }else {
            modify_address.setText("新增地址");
            container_address.setVisibility(View.GONE);
        }
    }

    //接受网络请求返回数据
    private void setNetResponse(Context context){
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("postAddressRequest")) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("type", "add");
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();  // 结束目标 Activity
                }
            }
            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postAddressRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (mLocationClient !=null){
            mLocationClient.onDestroy();
            mLocationClient =null;
        }
        if (geocoderSearch!=null){
            geocoderSearch=null;
        }
    }
}