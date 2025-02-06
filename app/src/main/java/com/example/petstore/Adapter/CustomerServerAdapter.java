package com.example.petstore.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petstore.R;
import com.example.petstore.pojo.Message;
import com.example.petstore.utils.CommonUtils;

import java.util.ArrayList;


public class CustomerServerAdapter extends RecyclerView.Adapter<CustomerServerAdapter.CustomerServerHolder> {
    private CommonUtils commonUtils = new CommonUtils();
    private ArrayList<Message> messageArrayList;
    private Context context;
    private String username;
    private AnimationDrawable anim;
    // 定义一个变量来记录当前正在播放动画的ViewHolder，初始化为null
    private Integer currentPlayingPosition = -1;
    private PlayVideoClickListener playVideoClickListener;

    // 定义接口
    public interface PlayVideoClickListener {
        void playVideo(String url);

        void playAudio(String url, AnimationDrawable anim);

        void stopAudio();

        void showImage(String url);
    }

    public CustomerServerAdapter(Context context, ArrayList<Message> messageArrayList, String username, PlayVideoClickListener playVideoClickListener) {
        this.context = context;
        this.messageArrayList = messageArrayList;
        this.username = username;
        this.playVideoClickListener = playVideoClickListener;
    }

    @NonNull
    @Override
    public CustomerServerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_service_item, parent, false);
        return new CustomerServerHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull CustomerServerHolder holder, int position) {

        Message message = messageArrayList.get(position);
        Integer messageType = message.getMessageType();

        holder.text_message.setVisibility(View.GONE);
        holder.image_message.setVisibility(View.GONE);
        holder.preview_container.setVisibility(View.GONE);

        holder.audio_container.setVisibility(View.GONE);
        holder.left_audio.setVisibility(View.GONE);
        holder.right_audio.setVisibility(View.GONE);
        holder.emp_avatar.setVisibility(View.GONE);
        holder.customer_avatar.setVisibility(View.GONE);


        // 根据消息发送者判断设置gravity属性
        if (message.getSender().equals(username)) {
            holder.message_container.setGravity(Gravity.RIGHT);
            holder.right_audio.setVisibility(View.VISIBLE);
            holder.customer_avatar.setVisibility(View.VISIBLE);

        } else {
            holder.message_container.setGravity(Gravity.LEFT);
            holder.left_audio.setVisibility(View.VISIBLE);
            holder.emp_avatar.setVisibility(View.VISIBLE);
        }

        // 判断消息类型
        // 0：文本消息 1：照片 2：视频 3：语音
        if (messageType == 0) {
            holder.text_message.setText(message.getMessage());
            holder.text_message.setVisibility(View.VISIBLE);
        } else if (messageType == 1) {
            Glide.with(context)
                    .load(message.getMessage())
                    .into(holder.image_message);
            holder.image_message.setVisibility(View.VISIBLE);

            holder.image_message.setOnClickListener(v -> {
                // 触发回调
                if (playVideoClickListener != null) {
                    playVideoClickListener.showImage(message.getMessage());
                }
            });
        } else if (messageType == 2) {

            Glide.with(context)
                    .load(message.getMessageCover())
                    .into(holder.video_preview);
            holder.video_time.setText(message.getMessageLength());

            holder.preview_container.setVisibility(View.VISIBLE);

            holder.preview_container.setOnClickListener(v -> {
                // 触发回调
                if (playVideoClickListener != null) {
                    playVideoClickListener.playVideo(message.getMessage());
                }
            });

        } else if (messageType == 3) {
            holder.audio_container.setVisibility(View.VISIBLE);
            holder.audio_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击当前位置
                    if (currentPlayingPosition == position && anim.isRunning()) {
                        stopAnim(anim);
                        return;
                    } else if (currentPlayingPosition != -1 && currentPlayingPosition != position) {
                        //停止上一个动画
                        CustomerServerHolder prevHolder = getViewHolderByPosition(currentPlayingPosition, holder.itemView);
                        if (prevHolder != null) {
                            AnimationDrawable prevAnim = null;
                            if (prevHolder.left_audio.getVisibility() == View.VISIBLE) {
                                prevAnim = (AnimationDrawable) prevHolder.left_audio.getBackground();
                            } else {
                                prevAnim = (AnimationDrawable) prevHolder.right_audio.getBackground();
                            }
                            stopAnim(prevAnim);
                        }
                    }


                    if (holder.left_audio.getVisibility() == View.VISIBLE) {
                        anim = (AnimationDrawable) holder.left_audio.getBackground();
                    } else {
                        anim = (AnimationDrawable) holder.right_audio.getBackground();
                    }
                    currentPlayingPosition = position;
                    startAnim(anim, message.getMessage());
                }
            });
        }


    }

    private void startAnim(AnimationDrawable anim, String url) {
        anim.start();
        // 触发播放回调
        if (playVideoClickListener != null) {
            playVideoClickListener.playAudio(url, anim);
        }
    }

    public void stopAnim(AnimationDrawable anim) {
        anim.stop();
        anim.selectDrawable(0);
        // 触发停止回调
        if (playVideoClickListener != null) {
            playVideoClickListener.stopAudio();
        }
    }

    private CustomerServerHolder getViewHolderByPosition(int position, View itemView) {
        RecyclerView recyclerView = (RecyclerView) itemView.getParent();
        return (CustomerServerHolder) recyclerView.findViewHolderForAdapterPosition(position);
    }


    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public class CustomerServerHolder extends RecyclerView.ViewHolder {
        TextView text_message, video_time;
        ImageView image_message, video_preview, left_audio, right_audio;
        FrameLayout preview_container;
        LinearLayoutCompat message_container, audio_container;
        View emp_avatar, customer_avatar;

        public CustomerServerHolder(View itemView) {
            super(itemView);
            text_message = itemView.findViewById(R.id.text_message);
            image_message = itemView.findViewById(R.id.image_message);

            video_time = itemView.findViewById(R.id.video_time);
            preview_container = itemView.findViewById(R.id.preview_container);
            video_preview = itemView.findViewById(R.id.video_preview);

            audio_container = itemView.findViewById(R.id.audio_container);
            left_audio = itemView.findViewById(R.id.left_audio);
            right_audio = itemView.findViewById(R.id.right_audio);

            emp_avatar = itemView.findViewById(R.id.emp_avatar);
            customer_avatar = itemView.findViewById(R.id.customer_avatar);


            message_container = itemView.findViewById(R.id.message_container);
        }
    }
}
