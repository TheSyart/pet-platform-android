package com.example.petstore.anotherActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petstore.Adapter.CustomerServerAdapter;
import com.example.petstore.Adapter.EmojiAdapter;
import com.example.petstore.Adapter.EmojiNavAdapter;
import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.dialogUtils.VoiceDialog;
import com.example.petstore.pojo.Message;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.utils.KeyboardVisibilityListener;
import com.example.petstore.utils.LoadPicture;
import com.example.petstore.utils.RecorderAudioManagerUtils;
import com.example.petstore.utils.WebSocketHandler;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomerServiceActivity extends AppCompatActivity implements CustomerServerAdapter.PlayVideoClickListener, RecorderAudioManagerUtils.RecordAudioListener, EmojiAdapter.EmojiClickListener {
    private WebSocketHandler webSocketHandler;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private JwtUtils jwtUtils;
    private NetRequest netRequest;
    private String username;

    // 存储未反转的原始数据
    private ArrayList<Message> originMessageList = new ArrayList<>();

    // 反转数据容器,存储正确顺序数据
    private ArrayList<Message> messageArrayList = new ArrayList();
    private RecyclerView message_recycler, emojiRecyclerView, emoji_nav_recyclerView;
    private CustomerServerAdapter customerServerAdapter;
    private EmojiAdapter emojiAdapter;
    private EmojiNavAdapter emojiNavAdapter;
    private EditText message_input;
    private ImageView back, speech_button, keyboard_button, emoji_button, more_button, image, video, close_video, image_show;
    private TextView speech;
    private LinearLayoutCompat bottom_container, more_list, emoji_list;
    private SmartRefreshLayout smartRefreshLayout;
    private LinearLayoutManager recyclerLayoutManager;

    // 聊天条数
    private int converseNum = 0;

    // 匹配的客服人员
    private String receiver = "";

    //接受类型 0员工 1客户 2系统
    private Integer receiverType = 2;
    // 判断存储消息的链接类型
    private String linkType = "";

    private FrameLayout video_container;
    private PlayerView playerView;
    private SimpleExoPlayer exoPlayer;


    /**
     * 语音录入处理-----------------------------------------------------------------
     */

    // 录音对话框
    private VoiceDialog voiceDialog;
    // 线程
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            3, 5,
            1, TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(10),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());
    // 录音工具类
    private RecorderAudioManagerUtils recorderAudioManagerUtils;

    //需要权限列表
    private String[] permissions = new String[]{
            android.Manifest.permission.RECORD_AUDIO};

    // 被用户拒绝的权限列表
    private List<String> mPermissionList = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST = 1001;

    // 播放音频的动画
    private AnimationDrawable playingAnim;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_service_activity);
        afterPermissions();
        init();
    }

    private void handleKeyboardVisibility(boolean isKeyboardVisible) {
        if (isKeyboardVisible) {
            // 获取LinearLayoutManager实例（假设是线性布局的RecyclerView）
            LinearLayoutManager layoutManager = (LinearLayoutManager) message_recycler.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPosition(customerServerAdapter.getItemCount() - 1);
            }
        }
    }

    public void init() {
        // 获取RecorderAudioManagerUtils单例实例
        recorderAudioManagerUtils = RecorderAudioManagerUtils.getInstance();
        recorderAudioManagerUtils.setRecordDataListener(this);

        // 初始化 ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        LoadPicture loadPicture = new LoadPicture();
                        loadPicture.onActivityResult(result.getResultCode(), data, this, linkType);

                        if (linkType.equals("Images")) {
                            handleImage(loadPicture.getImageBytes());
                        } else if (linkType.equals("Video")) {
                            handleVideo(loadPicture.getVideoBytes());
                        }

                    }
                });

        // 获取数据
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("loggedInUsername", null);

        // 添加键盘状态监听器
        new KeyboardVisibilityListener(this, this::handleKeyboardVisibility);

        // 监听键盘三方库
        KeyboardVisibilityEvent.setEventListener(
                this,
                isOpen -> {
                    if (isOpen) {
                        Log.d("KeyboardState", "软键盘弹出");
                        if (bottom_container.getVisibility() == View.VISIBLE) {
                            bottom_container.setVisibility(View.GONE);
                        }
                    } else {
                        Log.d("KeyboardState", "软键盘收起");
                    }
                }
        );

        setNetResponse();

        initWebSocket();

        componentInit();

        postNetResponse();
    }

    private void componentInit() {

        //初始化下拉刷新组件
        smartRefreshLayout = findViewById(R.id.smartRefresh);
        // 设置加载更多的监听器
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            smartRefreshLayout.finishLoadMore(500, true, false); // 假设没有更多数据了
        });

        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            System.out.println(" 当前聊天条数： " + converseNum);
            postNetResponse();
            // 完成加载更多，第一个参数是延迟时间，第二个参数是是否刷新成功，第三个参数是是否还有更多数据
            smartRefreshLayout.finishRefresh(500, true, false); // 假设没有更多数据了
        });

        image_show = findViewById(R.id.image_show);
        playerView = findViewById(R.id.playerView);
        exoPlayer = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        video_container = findViewById(R.id.video_container);

        close_video = findViewById(R.id.close_video);
        close_video.setOnClickListener(v -> {
            video_container.setVisibility(View.GONE);

            if (null == image_show.getDrawable()) {
                // 正在处理视频资源
                video_container.setVisibility(View.GONE);
                // 暂停播放
                exoPlayer.setPlayWhenReady(false);
                // 释放资源
                exoPlayer.release();
                // 可以根据需要，将 ExoPlayer 重新初始化，方便后续可能再次播放视频时使用，以下是简单示例
                exoPlayer = new SimpleExoPlayer.Builder(getApplicationContext()).build();
            } else {
                // 清除图片资源
                image_show.setImageDrawable(null);
            }
        });

        video = findViewById(R.id.video);
        video.setOnClickListener(v -> {
            linkType = "Video";
            LoadPicture loadPicture = new LoadPicture();
            loadPicture.getPictureByPhone(pickImageLauncher, "Video");
        });


        image = findViewById(R.id.image);
        image.setOnClickListener(v -> {
            linkType = "Images";
            LoadPicture loadPicture = new LoadPicture();
            loadPicture.getPictureByPhone(pickImageLauncher, "Images");
        });


        speech = findViewById(R.id.speech);
        speech.setOnClickListener(v -> voiceDialog = new VoiceDialog(CustomerServiceActivity.this, CustomerServiceActivity.this));

        bottom_container = findViewById(R.id.bottom_container);
        more_list = findViewById(R.id.more_list);
        emoji_list = findViewById(R.id.emoji_list);

        speech_button = findViewById(R.id.speech_button);
        speech_button.setOnClickListener(v -> {
            speech_button.setVisibility(View.GONE);
            keyboard_button.setVisibility(View.VISIBLE);
            speech.setVisibility(View.VISIBLE);
            message_input.setVisibility(View.GONE);
        });

        keyboard_button = findViewById(R.id.keyboard_button);
        keyboard_button.setOnClickListener(v -> {
            speech_button.setVisibility(View.VISIBLE);
            keyboard_button.setVisibility(View.GONE);
            speech.setVisibility(View.GONE);
            message_input.setVisibility(View.VISIBLE);
        });

        emoji_button = findViewById(R.id.emoji_button);
        emoji_button.setOnClickListener(v -> {
            closeKeyboard();
            bottom_container.setVisibility(View.VISIBLE);
            emoji_list.setVisibility(View.VISIBLE);
            more_list.setVisibility(View.GONE);
        });

        more_button = findViewById(R.id.more_button);
        more_button.setOnClickListener(v -> {
            closeKeyboard();
            bottom_container.setVisibility(View.VISIBLE);
            emoji_list.setVisibility(View.GONE);
            more_list.setVisibility(View.VISIBLE);
        });


        message_recycler = findViewById(R.id.message_recycler);
        message_recycler.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                bottom_container.setVisibility(View.GONE);
                return false;
            }
        });
        emojiRecyclerView = findViewById(R.id.emoji_recyclerView);
        emoji_nav_recyclerView = findViewById(R.id.emoji_nav_recyclerView);


        message_input = findViewById(R.id.message_input);
        // 设置OnEditorActionListener监听器
        message_input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // 这里是回车键被按下时执行的逻辑，获取EditText中的文本内容等操作
                sendMessage(0, null, null, null);
                return true;
            }
            return false;
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
    }

    private void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 获取当前获取焦点的视图，尝试从当前获取焦点的视图所在的窗口关闭软键盘
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void refreshMessage(int messageLength) {
        int itemCount = customerServerAdapter.getItemCount();
        if (messageLength == 1) {
            converseNum++;
            customerServerAdapter.notifyItemInserted(itemCount - 1);
            recyclerLayoutManager.scrollToPosition(itemCount - 1);
        } else if (messageLength > 1) {
            customerServerAdapter.notifyDataSetChanged();
            recyclerLayoutManager.scrollToPositionWithOffset(messageLength, 0);
        }
    }

    private void initAdapter(Context context) {
        // 表情初始化
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 6);
        emojiRecyclerView.setLayoutManager(gridLayoutManager);
        emojiAdapter = new EmojiAdapter(context, this);
        emojiRecyclerView.setAdapter(emojiAdapter);

        // 表情选择初始化
        emoji_nav_recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        emojiNavAdapter = new EmojiNavAdapter(context);
        emojiNavAdapter.setEmojiNavClickListener(new EmojiNavAdapter.EmojiNavClickListener() {
            @Override
            public void selectEmojiCategory(String category) {
                emojiAdapter.setNewEmojiList(category);

            }
        });
        emoji_nav_recyclerView.setAdapter(emojiNavAdapter);

        // 设置适配器
        customerServerAdapter = new CustomerServerAdapter(
                context,
                messageArrayList,
                username,
                this
        );
        message_recycler.setAdapter(customerServerAdapter);
        message_recycler.setLayoutManager(new LinearLayoutManager(context));

        // 初始化滚动一次
        // 获取LinearLayoutManager实例（是线性布局的RecyclerView）
        recyclerLayoutManager = (LinearLayoutManager) message_recycler.getLayoutManager();
        if (recyclerLayoutManager != null) {
            // 使用Handler的post方法延迟执行滚动到底部的操作
            new Handler().post(() -> {
                recyclerLayoutManager.scrollToPosition(customerServerAdapter.getItemCount() - 1);
            });
        }
    }

    private void postNetResponse() {
        netRequest.getMessageRequest("queryOnePersonMessage", username, "", converseNum);
    }

    //接受网络请求返回数据
    private void setNetResponse() {
        netRequest = new NetRequest(new JwtUtils(), getApplicationContext(), new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("getMessageRequest")) {
                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);

                    int messageLength = jsonResponse.length();
                    if (messageLength == 0) {
                        return;
                    }

                    messageArrayList.clear();

                    // 遍历 JSONArray
                    for (int i = 0; i < messageLength; i++) {
                        JSONObject json = jsonResponse.getJSONObject(i);

                        Message message = new Message(
                                json.getString("receiver"),
                                json.getInt("receiverType"),
                                json.getString("sender"),
                                json.getInt("sendType"),
                                json.getString("messageCover"),
                                json.getString("messageLength"),
                                json.getString("message"),
                                json.getInt("messageType")
                        );
                        originMessageList.add(message);
                    }
                    converseNum += messageLength;

                    messageArrayList.addAll(originMessageList);
                    // 使用 Collections.reverse 方法反转列表
                    Collections.reverse(messageArrayList);

                    if (customerServerAdapter == null) {
                        initAdapter(getApplicationContext());
                    } else {
                        // 通知适配器数据更新
                        refreshMessage(messageLength);
                    }

                }


                //0：文本消息 1：照片 2：视频 3：语音
                if (requestType.equals("upLoadFile")) {
                    Integer messageType = null;
                    if ("Images".equals(linkType)) {
                        messageType = 1;
                    } else if ("Video".equals(linkType)) {
                        messageType = 2;
                    } else if ("Audio".equals(linkType)) {
                        messageType = 3;
                    }

                    JSONObject json = new JSONObject(data);
                    sendMessage(
                            messageType,
                            json.getString("message"),
                            json.getString("messageCover"),
                            json.getString("messageLength")
                    );
                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("getMessageRequest")) {
                    Toast.makeText(getApplicationContext(), "数据加载失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("upLoadFile")) {
                    Toast.makeText(getApplicationContext(), "上传失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * websocket处理-------------------------------------------------------------------------------------------------------
     */

    private void initWebSocket() {
        jwtUtils = new JwtUtils();
        webSocketHandler = new WebSocketHandler(jwtUtils);

        // 设置自定义监听器，用于处理WebSocket连接错误信息
        webSocketHandler.setCustomWebSocketListener(new WebSocketHandler.CustomWebSocketListener() {
            @Override
            public void onWebSocketError(Throwable t) {
                runOnUiThread(() -> Toast.makeText(CustomerServiceActivity.this, "WebSocket 连接出错: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        // 设置接收消息监听器，处理接收到的服务器消息
        webSocketHandler.setOnMessageReceivedListener(new WebSocketHandler.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(String message) {
                runOnUiThread(() -> {
                    try {
                        handleMessage(message);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }

    //0：文本消息 1：照片 2：视频 3：语音
    private void sendMessage(Integer messageType, String linkMessage, String messageCover, String messageLength) {
        String message = "";
        if (messageType == 0) {
            message = message_input.getText().toString().trim();
        } else {
            message = linkMessage;
        }

        if (!message.isEmpty()) {
            // 添加到本地消息列表
            Message msg = new Message(
                    receiver,
                    receiverType,
                    username,
                    1,
                    messageCover,
                    messageLength,
                    message,
                    messageType
            );

            originMessageList.add(0, msg);
            messageArrayList.add(msg);
            refreshMessage(1);

            // 发送给后端的消息格式
            // sendType 1 客户发送; receiverType: 0 员工接受; messageType 0 文本消息;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("receiver", receiver);
                jsonObject.put("receiverType", receiverType);
                jsonObject.put("sender", username);
                jsonObject.put("sendType", 1);
                jsonObject.put("messageCover", messageCover);
                jsonObject.put("messageLength", messageLength);
                jsonObject.put("message", message);
                jsonObject.put("messageType", messageType);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            webSocketHandler.sendMessage(String.valueOf(jsonObject));
            linkType = "";
            message_input.setText("");
        } else {
            Toast.makeText(CustomerServiceActivity.this, "请输入要发送的消息内容", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleMessage(String msg) throws JSONException {
        JSONObject json = new JSONObject(msg);

        if (json.has("type")) {
            if ("assignCustomerServer".equals(json.getString("type"))) {
                if (json.getBoolean("result")) {
                    receiver = json.getString("username");
                    receiverType = 0;
                } else {
                    Toast.makeText(this, json.getString("reason"), Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Message message = new Message(
                    json.getString("receiver"),
                    json.getInt("receiverType"),
                    json.getString("sender"),
                    json.getInt("sendType"),
                    json.getString("messageCover"),
                    json.getString("messageLength"),
                    json.getString("message"),
                    json.getInt("messageType")
            );
            originMessageList.add(0, message);
            messageArrayList.add(message);
            refreshMessage(1);
        }
    }


    /**
     * 处理照片，视频，语音，新数据-------------------------------------------------------------------------------------------------------
     */

    private void handleImage(byte[] imageBytes) {
        // 将 byte[] 转换为 Base64 字符串
        if (imageBytes != null) {
            netRequest.upLoadFile(imageBytes, "converse/image", ".jpg", "converse");
        } else {
            Toast.makeText(this, "照片为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleVideo(byte[] videoBytes) {
        // 将 byte[] 转换为 Base64 字符串
        if (videoBytes != null) {
            netRequest.upLoadFile(videoBytes, "converse/video/body", ".mp4", "converse");
        } else {
            Toast.makeText(this, "视频为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAudio(byte[] audioBytes) {
        // 将 byte[] 转换为 Base64 字符串
        if (audioBytes != null) {
            netRequest.upLoadFile(audioBytes, "converse/audio", ".pcm", "converse");
        } else {
            Toast.makeText(this, "语音为空", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 生命周期-------------------------------------------------------------------------------------------------------
     */
    @Override
    protected void onStart() {
        super.onStart();
        playerView.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        RecorderAudioManagerUtils.getInstance().pauseAudio();
        playerView.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecorderAudioManagerUtils.getInstance().releaseAudio();
        exoPlayer.release();
        // 关闭WebSocket连接
        webSocketHandler.closeWebSocket();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 发起WebSocket连接，传入当前Activity的Context
        webSocketHandler.connectWebSocket(this);

        // 获取LinearLayoutManager实例（假设是线性布局的RecyclerView）
        LinearLayoutManager layoutManager = (LinearLayoutManager) message_recycler.getLayoutManager();
        if (layoutManager != null) {
            layoutManager.scrollToPosition(customerServerAdapter.getItemCount() - 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 音频播放-------------------------------------------------------------------------------------------------------
     */
    @Override
    public void playVideo(String url) {
        playerView.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(url);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);

        video_container.setVisibility(View.VISIBLE);
    }

    @Override
    public void showImage(String url) {
        Glide.with(getApplicationContext())
                .load(url)
                .into(image_show);
        video_container.setVisibility(View.VISIBLE);
    }

    // 开始录音
    public void startRecord() {
        threadPoolExecutor.execute(() -> {
            recorderAudioManagerUtils.startRecord(new WeakReference<>(getApplicationContext()));
        });

    }

    // 结束录音
    public void stopRecord() {
        recorderAudioManagerUtils.stopRecord();
    }

    // 获取录音数据结束回调
    @Override
    public void onRecordData(byte[] audioData) {
        linkType = "Audio";
        handleAudio(audioData);
    }


    // 播放语音
    @Override
    public void playAudio(String url, AnimationDrawable anim) {
        playingAnim = anim;
        threadPoolExecutor.execute(() -> {
            recorderAudioManagerUtils.playPcmData(new WeakReference<>(getApplicationContext()), url);
        });
    }

    // 结束播放语音
    @Override
    public void stopAudio() {
        recorderAudioManagerUtils.stopPcmData();
    }

    // 语音播放完回调
    @Override
    public void playAudioFinish() {
        customerServerAdapter.stopAnim(playingAnim);
    }

    private void afterPermissions() {
        // Marshmallow开始才用申请运行时权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) !=
                        PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (!mPermissionList.isEmpty()) {
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allGranted = true;

        for (int results : grantResults) {
            allGranted &= results == PackageManager.PERMISSION_GRANTED;
        }
        if (allGranted) {
            afterPermissions();
        } else {
            System.out.println("请打开" + mPermissionList + "权限");
            Toast.makeText(this, "请打开" + mPermissionList + "权限", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * emoji -------------------------------------------------------------------------------------------------------
     */
    @Override
    public void addEmoji(String emoji) {
        message_input.setText(message_input.getText() + emoji);
    }
}
