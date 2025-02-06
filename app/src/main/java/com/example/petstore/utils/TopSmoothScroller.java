package com.example.petstore.utils;
import android.content.Context;
import androidx.recyclerview.widget.LinearSmoothScroller;

public class TopSmoothScroller extends LinearSmoothScroller {

    public TopSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected int getVerticalSnapPreference() {
        return SNAP_TO_START; // 设置为SNAP_TO_START以确保最终在顶部
    }
}
