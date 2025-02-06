package com.example.petstore.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class KeyboardVisibilityListener implements LifecycleObserver   {
    private final View rootView;
    private final Lifecycle lifecycle;
    private final OnKeyboardVisibilityListener listener;
    private boolean wasKeyboardVisible = false;

    public KeyboardVisibilityListener(AppCompatActivity activity, OnKeyboardVisibilityListener listener) {
        this.rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        this.lifecycle = activity.getLifecycle();
        this.lifecycle.addObserver(this);
        this.listener = listener;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(this::onGlobalLayout);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this::onGlobalLayout);
    }

    private void onGlobalLayout() {
        int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
        boolean isKeyboardVisible = heightDiff > rootView.getHeight() / 3;
        if (isKeyboardVisible!= wasKeyboardVisible) {
            wasKeyboardVisible = isKeyboardVisible;
            listener.onKeyboardVisibilityChanged(isKeyboardVisible);
        }
    }

    public interface OnKeyboardVisibilityListener {
        void onKeyboardVisibilityChanged(boolean isKeyboardVisible);
    }
}
