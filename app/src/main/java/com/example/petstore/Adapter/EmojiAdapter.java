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
import com.example.petstore.utils.JsonUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiHolder> {
    private List<String> emojiList;
    private Context context;
    private EmojiClickListener emojiClickListener;
    // 定义接口
    public interface EmojiClickListener {
        void addEmoji(String emoji);
    }

    public EmojiAdapter(Context context, EmojiClickListener emojiClickListener) {
        this.context = context;
        this.emojiList = getEmoji("😂");
        this.emojiClickListener = emojiClickListener;
    }


    @NonNull
    @Override
    public EmojiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.emoji_item, parent, false);
        return new EmojiHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull EmojiHolder holder, int position) {
        String emoji = emojiList.get(position);
        holder.emoji.setText(emoji);
        holder.itemView.setOnClickListener(v -> {
            if (emojiClickListener != null){
                holder.emoji.setBackgroundResource(R.drawable.select_emoji);
                emojiClickListener.addEmoji(emoji);
            }
        });
    }

    @Override
    public int getItemCount() {
        return emojiList.size();
    }


    public class EmojiHolder extends RecyclerView.ViewHolder {
        TextView emoji;

        public EmojiHolder(View itemView) {
            super(itemView);
            emoji = itemView.findViewById(R.id.emoji);
        }
    }

    public List<String> getEmoji(String category) {
        List<String> emojiList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(JsonUtils.loadJsonFromAsset(context, "newEmoji.json"));
            if (jsonObject.has(category)) {
                JSONArray jsonArray = jsonObject.getJSONArray(category);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject emojiObject = jsonArray.getJSONObject(i);
                    String emoji = emojiObject.getString("emoji");
                    emojiList.add(emoji);
                }
            }
            System.out.println(emojiList);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return emojiList;
    }

    public void setNewEmojiList(String category){
        emojiList.clear();
        emojiList = getEmoji(category);
        notifyDataSetChanged();
    }

}


