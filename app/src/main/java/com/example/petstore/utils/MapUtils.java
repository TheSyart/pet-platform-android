package com.example.petstore.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.example.petstore.Adapter.PoiAdapter;
import com.example.petstore.R;
import com.example.petstore.pojo.Bean;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class MapUtils implements AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener{
    private AMap aMap;
    private AMapLocationClient mLocationClient;
    private PoiAdapter poiAdapter;
    private Marker marker;
    private String city;
    private GeocodeSearch geocoderSearch;
    private Context context;
    private Activity activity;
    private List<Bean> beanList;
    public MapUtils(Activity activity, Context context, PoiAdapter poiAdapter, AMap aMap, List<Bean> beanList) {
        this.activity = activity;
        this.context = context;
        this.poiAdapter = poiAdapter;
        this.aMap = aMap;
    }
    /**
     * 搜索tip
     **/
    public void showTip(String input, List<Bean> list){
        beanList = list;
        InputtipsQuery inputQuery = new InputtipsQuery(input, city);
        Inputtips inputTips = new Inputtips(context, inputQuery);
        inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> tipList, int rCode) {
                if (rCode == AMapException.CODE_AMAP_SUCCESS) {// 正确返回
                    if (tipList.size()!=0){
                        beanList.clear();
                        for (Tip tip:tipList){
                            if (tip.getPoint()!=null){
                                beanList.add(new Bean(tip.getName(),tip.getAddress(),tip.getPoint().getLatitude(),tip.getPoint().getLongitude()));
                            }
                        }
                        poiAdapter.notifyDataSetChanged();
                        aMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(tipList.get(0).getPoint().getLatitude(),tipList.get(0).getPoint().getLongitude()))); //设置地图中心点
                    }
                } else {
                    Toast.makeText(context, "错误编码: " + rCode, Toast.LENGTH_SHORT).show();
                }
            }
        });
        inputTips.requestInputtipsAsyn();
    }
    /**
     * 权限请求
     **/
    public void permission(){
        String[] permission;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permission=new String[]{
                    Permission.ACCESS_COARSE_LOCATION,
                    Permission.ACCESS_FINE_LOCATION,
                    Permission.MANAGE_EXTERNAL_STORAGE,
                    Permission.READ_PHONE_STATE,
            };
        }
        else {
            permission=new String[]{
                    Permission.ACCESS_COARSE_LOCATION,
                    Permission.ACCESS_FINE_LOCATION,
                    Permission.READ_PHONE_STATE,
            };
        }
        XXPermissions.with(activity)
                .permission(permission).request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (allGranted){
                            initLocation();
                            startLocation();
                        }
                        else {
                            Toast.makeText(context, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain){
                            XXPermissions.startPermissionActivity(context,permissions);
                        }
                        else {
                            Toast.makeText(context, "权限获取失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    /**
     * 初始化定位
     **/
    private void initLocation(){
        //设置定位蓝点
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //设置缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        // 控件交互 缩放按钮、指南针、定位按钮、比例尺等
        UiSettings mUiSettings;//定义一个UiSettings对象
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(true); //显示默认的定位按钮
        aMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置
        mUiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示
        mUiSettings.setLogoPosition(AMapOptions.LOGO_MARGIN_LEFT);//设置logo位置
    }
    /**
     * 开始定位
     **/
    private void startLocation() {
        try {
            //获取位置信息
            mLocationClient = new AMapLocationClient(context);
        }catch (Exception e){
            e.printStackTrace();
        }
        //初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mLocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果
        mLocationOption.setOnceLocationLatest(true);
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }
    /**
     * 定位回调
     **/
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
            LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            setMapCenter(latLng);
            city = aMapLocation.getCity();
            aMap.animateCamera(CameraUpdateFactory.newLatLng(latLng)); //设置地图中心点
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }
    /**
     * 设置中心点
     **/
    public void setMapCenter(LatLng latLng) {
        MarkerOptions markerOptions=new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(context.getResources(), R.drawable.baseline_location_on_24)))
                .position(latLng);
        if (marker!=null){
            marker.remove();
        }
        marker = aMap.addMarker(markerOptions);
        //坐标系转换，坐标信息转换为屏幕的中心点信息
        Point markerPoint = aMap.getProjection().toScreenLocation(latLng);
        marker.setFixingPointEnable(true);
        marker.setPositionByPixels(markerPoint.x, markerPoint.y);
        //marker动画
        Point point =  aMap.getProjection().toScreenLocation(latLng);

        point.y -= dip2px(context);
        LatLng target = aMap.getProjection().fromScreenLocation(point);
        //使用TranslateAnimation,填写一个需要移动的目标点
        Animation animation = new TranslateAnimation(target);
        animation.setInterpolator(new LinearInterpolator());
        long duration = 300;
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());
        marker.setAnimation(animation);
        marker.startAnimation();
    }
    /**
     * dip 和 px转换
     **/
    private static int dip2px(Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((float) 30 * scale + 0.5f);
    }
    /**
     * 逆地理编码获取当前位置信息
     **/
    public void getGeocodeSearch(LatLng target) throws AMapException {
        if (geocoderSearch == null){
            geocoderSearch = new GeocodeSearch(context);
            geocoderSearch.setOnGeocodeSearchListener(this);
        }
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(target.latitude, target.longitude), 500, GeocodeSearch.AMAP);
        query.setExtensions("all");
        geocoderSearch.getFromLocationAsyn(query);
    }
    /**
     * 地理编码
     **/
    private void searchLocation(String address) throws AMapException {
        if (geocoderSearch == null){
            geocoderSearch = new GeocodeSearch(context);
            geocoderSearch.setOnGeocodeSearchListener(this);
        }
        GeocodeQuery query=new GeocodeQuery(address,city);
        geocoderSearch.getFromLocationNameAsyn(query);
    }
    /**
     * 逆地理编码回调
     **/
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        System.out.println("GPS :i"+ i);
        if (i != 1000)return;

        if (beanList == null) {
            beanList = new ArrayList<>();
        }

        beanList.clear();

        List<PoiItem> poiItems = regeocodeResult.getRegeocodeAddress().getPois();
        //beanList.add(new Bean("当前位置",regeocodeResult.getRegeocodeAddress().getFormatAddress(),regeocodeResult.getRegeocodeQuery().getPoint().getLatitude(),regeocodeResult.getRegeocodeQuery().getPoint().getLongitude()));
        String province = regeocodeResult.getRegeocodeAddress().getProvince();
        String city = regeocodeResult.getRegeocodeAddress().getCity();
        for (PoiItem poiItem : poiItems) {
            String addressDetails;

            // 检查是否为直辖市
            if (province.equals(city)) {
                // 直辖市时省份与城市名称相同
                addressDetails = city + poiItem.getSnippet();
            } else {
                // 非直辖市时，省份和城市名称不同
                addressDetails = province + city + poiItem.getSnippet();
            }
            beanList.add(new Bean(poiItem.getTitle(), addressDetails, poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude()));
            poiAdapter.setData(beanList);
        }
    }
    /**
     * 地理编码回调
     **/
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (geocodeResult!=null){
            List<GeocodeAddress> geocodeAddressList = geocodeResult.getGeocodeAddressList();
            if (geocodeAddressList!=null&&geocodeAddressList.size()!=0){
                GeocodeAddress address = geocodeAddressList.get(0);
            }
        }
    }
}
