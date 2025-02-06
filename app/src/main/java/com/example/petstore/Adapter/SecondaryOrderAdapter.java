package com.example.petstore.Adapter;

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

public class SecondaryOrderAdapter extends RecyclerView.Adapter<SecondaryOrderAdapter.SecondaryHolder> {
    private Context context;
    private List<OrderInfo> orderInfo;

    public SecondaryOrderAdapter(Context context, List<OrderInfo> orderInfo) {
        this.context = context;
        this.orderInfo = orderInfo;
    }

    @NonNull
    @Override
    public SecondaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.secondary_order_item, parent, false);
        return new SecondaryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SecondaryHolder holder, int position) {
        holder.product_name.setText(orderInfo.get(position).getName());
        holder.product_quantity.setText(String.valueOf(orderInfo.get(position).getQuantity()));
        holder.product_price.setText(String.valueOf(orderInfo.get(position).getPrice()));
        Glide.with(context)
                .load(orderInfo.get(position).getImage_path())
                .into(holder.product_image);

    }

    @Override
    public int getItemCount() {
        return orderInfo.size();
    }

    public static class SecondaryHolder extends RecyclerView.ViewHolder {
        TextView product_name, product_price, product_quantity;
        ImageView product_image;

        public SecondaryHolder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            product_quantity = itemView.findViewById(R.id.product_quantity);
            product_image = itemView.findViewById(R.id.product_image);
        }
    }
}

