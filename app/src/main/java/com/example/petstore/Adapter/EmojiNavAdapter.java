package com.example.petstore.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petstore.R;
import java.util.Arrays;
import java.util.List;

public class EmojiNavAdapter extends RecyclerView.Adapter<EmojiNavAdapter.EmojiNavHolder> {
    // 使用 Arrays.asList() 方法初始化 List
    private List<String> emojiNavList;
    private Context context;
    private int currentPosition = 0;
    private EmojiNavClickListener emojiNavClickListener;

    // 定义接口
    public void setEmojiNavClickListener(EmojiNavClickListener emojiNavClickListener) {
        this.emojiNavClickListener = emojiNavClickListener;
    }

    public interface EmojiNavClickListener {
        void selectEmojiCategory(String category);
    }

    public EmojiNavAdapter(Context context) {
        this.context = context;
        this.emojiNavList = getEmojiCategory();
    }


    @NonNull
    @Override
    public EmojiNavHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.emoji_nav_item, parent, false);
        return new EmojiNavHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull EmojiNavHolder holder, int position) {
        String category = emojiNavList.get(position);
        holder.emoji_nav.setText(category);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojiNavClickListener != null){
                    emojiNavClickListener.selectEmojiCategory(category);
                    currentPosition = position;
                    notifyDataSetChanged();
                }
            }
        });

        if (currentPosition == position){
            holder.itemView.setBackgroundResource(R.drawable.select_emoji);
        }else {
            holder.itemView.setBackground(null);
        }

    }

    @Override
    public int getItemCount() {
        return emojiNavList.size();
    }


    public class EmojiNavHolder extends RecyclerView.ViewHolder {
        TextView emoji_nav;

        public EmojiNavHolder(View itemView) {
            super(itemView);
            emoji_nav = itemView.findViewById(R.id.emoji_nav);
        }
    }

    public List<String> getEmojiCategory(){
        List<String> emojiNavList = Arrays.asList("😂", "🏃‍♂️", "🏼", "🐵", "🍓", "🚌", "⚽", "⌚", "💯", "🏴");
        return emojiNavList;
    }
}
