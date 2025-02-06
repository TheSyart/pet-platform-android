package com.example.petstore.interfaces;

import com.example.petstore.pojo.MyPet;

public interface MyPetServiceInterface {
    // 当宠物被选中时，这个方法会被调用
    void onPetSelected(MyPet myPet);

    // 打开新增宠物对话框
    void startAddMyPetDialog();

    //打开悬浮球
    void openFloatingBall();

    // 订单完成，跳转界面
    void navigateToOrder();
}
