package com.example.petstore.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.petstore.R;
import com.example.petstore.interfaces.FragmentInteractionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PetPlatformActivity extends AppCompatActivity implements FragmentInteractionListener {
    private FragmentManager fragmentManager;
    private BottomNavigationView bnv;
    private Fragment currentFragment; // 当前显示的Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_platform_activity);

        //先获取FragmentManager对象
        fragmentManager = getSupportFragmentManager();
        currentFragment = new HomeActivity(); // 默认首页
        loadFragment(currentFragment); // 加载默认的首页


        bnv = findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.home){
                    selectedFragment = new HomeActivity();
                }
                if (item.getItemId() == R.id.shopping){
                    selectedFragment = new ShoppingActivity();
                }
                if (item.getItemId() == R.id.serve){
                    selectedFragment = new ServiceActivity();
                }
                if (item.getItemId() == R.id.order){
                    selectedFragment = new OrderActivity();
                }
                if (item.getItemId() == R.id.person){
                    selectedFragment = new PersonActivity();
                }

                // 判断是否是当前显示的 Fragment，如果是就不做任何操作
                if (selectedFragment != null && !selectedFragment.getClass().equals(currentFragment.getClass())) {
                    loadFragment(selectedFragment);
                }

                return true;
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        //开启一个FragmentTransaction事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //新页面替换旧页面
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        //默认的返回栈行为
        fragmentTransaction.addToBackStack(null);
        //提交事务
        fragmentTransaction.commit();
        //新界面赋值给currentFragment
        currentFragment = fragment;
        System.out.println("currentFragment" + currentFragment.getClass());
    }

    @Override
    public void onFragmentInteraction(Fragment fragment) {
        // 根据当前 Fragment 更新 BottomNavigationView 的选中项
        if (fragment instanceof HomeActivity) {
            bnv.setSelectedItemId(R.id.home);
        } else if (fragment instanceof ShoppingActivity) {
            bnv.setSelectedItemId(R.id.shopping);
        } else if (fragment instanceof ServiceActivity) {
            bnv.setSelectedItemId(R.id.serve);
        } else if (fragment instanceof OrderActivity) {
            bnv.setSelectedItemId(R.id.order);
        } else if (fragment instanceof PersonActivity) {
            bnv.setSelectedItemId(R.id.person);
        }
    }
}
