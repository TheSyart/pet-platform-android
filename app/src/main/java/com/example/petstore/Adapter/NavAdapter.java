package com.example.petstore.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petstore.R;
import com.example.petstore.activity.ShoppingActivity;

import java.util.List;

public class NavAdapter extends RecyclerView.Adapter<NavAdapter.NavViewHolder> {
    private List<String> navList;
    private ShoppingActivity shoppingActivity;
    private NavOnClickItemListener navOnClickItemListener;
    private int currentPosition = 0;
    public NavAdapter(ShoppingActivity shoppingActivity, List<String> navList) {
        this.shoppingActivity = shoppingActivity;
        this.navList = navList;
    }

    @NonNull
    @Override
    public NavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(shoppingActivity.getContext()).inflate(R.layout.nav_item, parent, false);
        return new NavViewHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull NavViewHolder holder, int position) {
        holder.nav_text.setText(navList.get(position));

        // 设置背景颜色：高亮当前选中项
        int backgroundResource = (currentPosition == position) ? R.drawable.select_yes : R.drawable.select_no;
        holder.itemView.setBackgroundResource(backgroundResource);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 使用 holder.getAdapterPosition() 获取当前位置
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    shoppingActivity.scrollToCategory(currentPosition);
                }

                if (null != navOnClickItemListener){
                    navOnClickItemListener.onItemclick(position);
                }
            }
        });

        if (currentPosition == position){
            holder.itemView.setBackgroundResource(R.drawable.select_yes);
        }else {
            holder.itemView.setBackgroundResource(R.drawable.select_no);
        }
    }

    @Override
    public int getItemCount() {
        return navList.size();
    }

    public class NavViewHolder extends RecyclerView.ViewHolder {
        TextView nav_text;

        public NavViewHolder(View itemView) {
            super(itemView);
            nav_text = itemView.findViewById(R.id.nav_text);
        }
    }

    public void setNavOnClickItemListener(NavOnClickItemListener navOnClickItemListener) {
        this.navOnClickItemListener = navOnClickItemListener;
    }

    public interface NavOnClickItemListener{
        void onItemclick(int position);
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        //通知适配器
        notifyDataSetChanged();
    }
}


