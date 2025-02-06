package com.example.petstore.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.petstore.R;
import com.example.petstore.pojo.OrderInfo;

import java.util.List;

public class PayAdapter extends RecyclerView.Adapter<PayAdapter.PayHolder> {
    private List<OrderInfo> orderInfoList;
    private Context context;

    public PayAdapter(Context context, List<OrderInfo> orderInfoList) {
        this.context = context;
        this.orderInfoList = orderInfoList;
    }

    @NonNull
    @Override
    public PayAdapter.PayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pay_item, parent, false);
        return new PayAdapter.PayHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull PayAdapter.PayHolder holder, int position) {
        Glide.with(context)
                .load(orderInfoList.get(position).getImage_path())
                .into(holder.image);
        holder.name.setText(orderInfoList.get(position).getName());
        holder.one_price.setText(String.valueOf(orderInfoList.get(position).getPrice()));
        holder.num.setText(String.valueOf(orderInfoList.get(position).getQuantity()));
        holder.total_price.setText(String.valueOf(orderInfoList.get(position).getSubtotal()));
    }

    @Override
    public int getItemCount() {
        return orderInfoList.size();
    }


    public class PayHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView one_price;
        TextView total_price;
        ImageView image;
        TextView num;

        public PayHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            one_price = itemView.findViewById(R.id.one_price);
            total_price = itemView.findViewById(R.id.total_price);
            image = itemView.findViewById(R.id.image);
            num = itemView.findViewById(R.id.num);
        }
    }
}

