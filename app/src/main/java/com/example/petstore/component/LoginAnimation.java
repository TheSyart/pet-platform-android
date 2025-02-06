package com.example.petstore.component;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;


public class LoginAnimation {
    private View view;
    private View progress;

    public LoginAnimation(final View view, View progress){
        this.view = view;
        this.progress = progress;
    }
    /**
     * 输入框的动画效果
     *
     * @param w        输入框动画的宽度变化范围的最大值
     * @param h        输入框动画的高度（虽然在当前代码中未使用，但可作为参数传递进来以备后续扩展）
     */
    public void inputAnimator(float w, float h) {
        // 创建一个动画集合，用于组合多个动画效果
        AnimatorSet set = new AnimatorSet();

        // 创建一个值动画，从 0 到 w 的范围进行动画
        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        // 为值动画添加更新监听器，在动画更新时修改输入框的边距
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 获取当前动画的值
                float value = (Float) animation.getAnimatedValue();
                // 获取输入框的布局参数
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                // 设置输入框的左右边距为当前动画的值
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                // 将修改后的布局参数应用到输入框
                view.setLayoutParams(params);
            }
        });

        // 创建一个对象动画，对输入框的 X 轴缩放进行动画，从原始比例 1 缩小到 0.5
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.5f);
        // 设置动画集合的持续时间为 1000 毫秒
        set.setDuration(1000);
        // 使用加速减速插值器，使动画开始和结束时速度较慢，中间速度较快
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        // 将值动画和对象动画一起播放
        set.playTogether(animator, animator2);
        // 开始动画
        set.start();
        // 为动画集合添加监听器，监听动画的各个状态
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // 动画开始时的操作，这里为空
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // 动画重复时的操作，这里为空
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束时的操作
                // 将进度视图设置为可见
                progress.setVisibility(View.VISIBLE);
                // 调用进度动画方法，对进度视图进行动画操作
                progressAnimator(progress);
                // 将输入框视图设置为不可见
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // 动画取消时的操作，这里为空
            }
        });
    }


    /**
     * 出现进度动画
     *
     * @param view 要进行动画操作的进度视图
     */
    private void progressAnimator(final View view) {
        // 创建属性值持有者，对进度视图的 X 轴缩放进行动画，从 0.5 到 1
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1f);
        // 创建属性值持有者，对进度视图的 Y 轴缩放进行动画，从 0.5 到 1
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1f);
        // 创建对象动画，将两个属性值持有者应用到进度视图
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view, animator, animator2);
        // 设置动画持续时间为 1000 毫秒
        animator3.setDuration(1000);
        // 使用自定义的 JellyInterpolator 插值器（需要确保该类存在且正确导入）
        animator3.setInterpolator(new JellyInterpolator());
        // 开始动画
        animator3.start();
    }


    /**
     * 恢复初始状态
     * 该方法目前被注释，可能是用于将输入框和进度视图恢复到原始状态
     * 如将进度视图隐藏，输入框及其相关子视图显示，重置输入框边距和缩放比例等
     */
    public void recovery() {
        progress.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        view.setLayoutParams(params);


        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleX", 0.5f,1f );
        animator2.setDuration(500);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.start();
    }
}