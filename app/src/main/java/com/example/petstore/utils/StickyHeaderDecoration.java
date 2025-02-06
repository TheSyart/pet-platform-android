package com.example.petstore.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petstore.Adapter.CommodityAdapter;

public class StickyHeaderDecoration extends RecyclerView.ItemDecoration {
    private int groupHeaderHeight;
    private Paint headerPaint;
    private Paint textPaint;
    private Rect textRect;

    public StickyHeaderDecoration(Context context) {
        groupHeaderHeight = dp2px(context, 100);
        headerPaint = new Paint();
        headerPaint.setColor(Color.parseColor("#f4f4f4"));
        headerPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(dp2px(context, 28));
        textPaint.setAntiAlias(true);
        textRect = new Rect();


    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (parent.getAdapter() instanceof CommodityAdapter) {
            CommodityAdapter adapter = (CommodityAdapter) parent.getAdapter();
            int childCount = parent.getChildCount();
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 0; i < childCount; i++) {
                View childAt = parent.getChildAt(i);
                int childLayoutPosition = parent.getChildLayoutPosition(childAt);
                boolean isGroupHeader = adapter.isGroupHeader(childLayoutPosition);

                if (isGroupHeader) {
                    c.drawRect(left, childAt.getTop() - groupHeaderHeight, right, childAt.getTop(), headerPaint);
                    String groupName = adapter.getGroupName(childLayoutPosition);

                    textPaint.getTextBounds(groupName, 0, groupName.length(), textRect);
                    c.drawText(groupName, left + 20, childAt.getTop() - groupHeaderHeight / 2 + textRect.height() / 2, textPaint);
                } else if (childAt.getTop() - groupHeaderHeight - parent.getPaddingTop() >= 0) {
                    c.drawRect(left, childAt.getTop() - 1, right, childAt.getTop(), headerPaint);
                }
            }
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (parent.getAdapter() instanceof CommodityAdapter) {
            CommodityAdapter adapter = (CommodityAdapter) parent.getAdapter();
            // 屏幕可视的第一个 itemView 的位置
            int firstVisibleItemPosition = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();

            // 获取 position 对应的 view
            RecyclerView.ViewHolder viewHolder = parent.findViewHolderForAdapterPosition(firstVisibleItemPosition);

            if (viewHolder != null) {
                View itemView = viewHolder.itemView;
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();
                int top = parent.getPaddingTop();

                // 当屏幕可视范围内，第二个 itemView 是下一组的头部的时候
                boolean isGroupHeader = adapter.isGroupHeader(firstVisibleItemPosition + 1);

                if (isGroupHeader) {
                    // 这种情况就要将上一个吸顶的慢慢往上顶的效果
                    int bottom = Math.min(groupHeaderHeight, itemView.getBottom() - parent.getPaddingTop());
                    c.drawRect(left, top, right, top + bottom, headerPaint);
                    String groupName = adapter.getGroupName(firstVisibleItemPosition);
                    textPaint.getTextBounds(groupName, 0, groupName.length(), textRect);
                    c.drawText(groupName, left + 20, top + bottom - groupHeaderHeight / 2 + textRect.height() / 2, textPaint);
                } else {
                    // 固定在顶部的效果
                    c.drawRect(left, top, right, top + groupHeaderHeight, headerPaint);
                    String groupName = adapter.getGroupName(firstVisibleItemPosition);
                    textPaint.getTextBounds(groupName, 0, groupName.length(), textRect);
                    c.drawText(groupName, left + 20, top + groupHeaderHeight / 2 + textRect.height() / 2, textPaint);
                }
            }
        }
    }









    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getAdapter() instanceof CommodityAdapter) {
            CommodityAdapter adapter = (CommodityAdapter) parent.getAdapter();
            int childLayoutPosition = parent.getChildLayoutPosition(view);
            boolean isGroupHeader = adapter.isGroupHeader(childLayoutPosition);

            int totalItemCount = parent.getAdapter().getItemCount();
            boolean isLastItem = childLayoutPosition == totalItemCount - 1;

            if (isGroupHeader) {
                outRect.set(0, groupHeaderHeight, 0, 0);
            }else if (isLastItem) {
                // 设置最后一个子项下方的偏移量
                outRect.set(0, 0, 0, 500);
            } else {
                outRect.set(0, 1, 0, 0);
            }
        }
    }



    private int dp2px(Context context, float dpValve) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValve * density * 0.5f);
    }
}