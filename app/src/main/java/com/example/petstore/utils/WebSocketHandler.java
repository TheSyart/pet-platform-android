package com.example.petstore.utils;

import android.content.Context;
import android.util.Log;

import com.example.petstore.R;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketHandler {
    private static String ipAddress;
    private JwtUtils jwtUtils;
    private final OkHttpClient client = new OkHttpClient();
    private WebSocket webSocket;
    // 自定义监听器，用于向外传递WebSocket连接过程中的错误信息
    private CustomWebSocketListener customWebSocketListener;
    // 用于接收消息的监听器
    private OnMessageReceivedListener onMessageReceivedListener;

    public WebSocketHandler(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    // 设置自定义监听器（处理错误信息）的方法
    public void setCustomWebSocketListener(CustomWebSocketListener listener) {
        this.customWebSocketListener = listener;
    }

    // 设置接收消息监听器的方法
    public void setOnMessageReceivedListener(OnMessageReceivedListener listener) {
        this.onMessageReceivedListener = listener;
    }

    // 连接WebSocket的方法
    public void connectWebSocket(Context context) {
        ipAddress = context.getResources().getString(R.string.ip_address);
        Request request = new Request.Builder()
                .url("ws://" + ipAddress + ":8080/ws?token=" + jwtUtils.getJwt(context))
                .build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                Log.d("WebSocketHandler", "WebSocket连接成功");
                // 连接成功后的回调，可以在这里进行一些初始化操作，比如发送认证消息等
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                Log.d("WebSocketHandler", "接收到服务器消息: " + text);
                if (onMessageReceivedListener != null) {
                    onMessageReceivedListener.onMessageReceived(text);
                }
                // 接收到服务器发送的消息时的回调，text就是接收到的消息内容，通过监听器向外传递消息
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                Log.d("WebSocketHandler", "WebSocket连接正在关闭，代码: " + code + ", 原因: " + reason);
                // 连接正在关闭时的回调
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d("WebSocketHandler", "WebSocket连接已关闭，代码: " + code + ", 原因: " + reason);
                // 连接已经关闭后的回调
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                Log.e("WebSocketHandler", "WebSocket连接出现故障", t);
                if (customWebSocketListener != null) {
                    customWebSocketListener.onWebSocketError(t);
                }
                // 连接出现故障时的回调，比如网络问题等
            }
        });
    }

    // 发送消息的方法
    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        } else {
            Log.e("WebSocketHandler", "WebSocket未连接，无法发送消息");
        }
    }


    // 关闭WebSocket连接的方法
    public void closeWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "正常关闭");
        }
    }

    // 自定义的WebSocket监听器接口，用于向外传递错误信息
    public interface CustomWebSocketListener {
        void onWebSocketError(Throwable t);
    }

    // 用于接收消息的监听器接口
    public interface OnMessageReceivedListener {
        void onMessageReceived(String message);
    }
}