package com.example.petstore.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class AndroidBug5497Workaround {
    public static void assistActivity(Activity activity) {
        new AndroidBug5497Workaround(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private int contentHeight;
    private boolean isfirst = true;
    private Activity activity;

    private AndroidBug5497Workaround(Activity activity) {
        this.activity = activity;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        if (mChildOfContent!= null) {
            onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    if (isfirst) {
                        contentHeight = mChildOfContent.getHeight();
                        isfirst = false;
                    }
                    Rect r = new Rect();
                    mChildOfContent.getWindowVisibleDisplayFrame(r);
                    int usableHeightNow = (r.bottom - r.top);
                    if (usableHeightNow!= usableHeightPrevious) {
                        int usableHeightSansKeyboard = mChildOfContent.getHeight();
                        int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                        if (heightDifference > (usableHeightSansKeyboard / 4)) {
                            // 键盘弹出，进行相应布局调整逻辑
                            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
                            lp.height = usableHeightSansKeyboard - heightDifference;
                            mChildOfContent.setLayoutParams(lp);
                        } else {
                            // 键盘隐藏，恢复布局逻辑
                            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
                            if (lp.height!= contentHeight) {
                                lp.height = contentHeight;
                                mChildOfContent.setLayoutParams(lp);
                            }
                        }
                        usableHeightPrevious = usableHeightNow;
                    }
                }
            };
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }
}