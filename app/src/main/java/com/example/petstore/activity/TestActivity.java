//package com.example.petstore.activity;
//
//import android.animation.Animator;
//import android.animation.AnimatorSet;
//import android.animation.ObjectAnimator;
//import android.animation.PropertyValuesHolder;
//import android.animation.ValueAnimator;
//import android.os.Bundle;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.animation.AccelerateDecelerateInterpolator;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//import com.example.petstore.R;
//import com.example.petstore.component.JellyInterpolator;
//import com.example.petstore.component.LoginAnimation;
//
//
//public class TestActivity extends AppCompatActivity implements View.OnClickListener {
//    private TextView mBtnLogin;
//
//    private View progress;
//
//    private View mInputLayout;
//
//    private float mWidth, mHeight;
//
//    private LinearLayout mName, mPsw;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.test_activity);
//
//        initView();
//    }
//
//    private void initView() {
//        mBtnLogin = (TextView) findViewById(R.id.main_btn_login);
//        progress = findViewById(R.id.layout_progress);
//        mInputLayout = findViewById(R.id.input_layout);
//        mName = (LinearLayout) findViewById(R.id.input_layout_name);
//        mPsw = (LinearLayout) findViewById(R.id.input_layout_psw);
//
//        mBtnLogin.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//
//        // 计算出控件的高与宽
//        mWidth = mBtnLogin.getMeasuredWidth();
//        mHeight = mBtnLogin.getMeasuredHeight();
//        // 隐藏输入框
//        mName.setVisibility(View.INVISIBLE);
//        mPsw.setVisibility(View.INVISIBLE);
//
//        LoginAnimation loginAnimation = new LoginAnimation();
//        loginAnimation.inputAnimator(mInputLayout, progress, mWidth, mHeight);
//
//    }
//
//
//}
