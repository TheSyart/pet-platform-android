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
import com.example.petstore.activity.ServiceActivity;
import com.example.petstore.dialogUtils.ShowShoppingDialog;
import com.example.petstore.pojo.ServiceInfo;
import com.example.petstore.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceHolder> {
    private ArrayList<ServiceInfo> serviceInfos;
    private Context context;
    private Boolean[] count;
    private ServiceActivity serviceActivity;

    public ServiceAdapter(Context context, ArrayList<ServiceInfo> serviceInfos, ServiceActivity serviceActivity) {
        this.context = context;
        this.serviceInfos = serviceInfos;
        this.serviceActivity = serviceActivity;

        if (serviceInfos != null && !serviceInfos.isEmpty() && count == null){
            // 初始化 count 数组
            count = new Boolean[serviceInfos.size()];
            for (int i = 0; i < serviceInfos.size(); i++) {
                count[i] = false; // 初始化为 false
            }
        }
    }

    @NonNull
    @Override
    public ServiceAdapter.ServiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_item, parent, false);
        return new  ServiceAdapter.ServiceHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull ServiceAdapter.ServiceHolder holder, int position) {
        //判断该服务是否已选择
        if (count[position]){
            holder.ifPay.setText("已选择");
            holder.ifPay.setBackground(holder.ifPay.getResources().getDrawable(R.drawable.service_itembg_two));
        }else {
            holder.ifPay.setText("未选择");
            holder.ifPay.setBackground(holder.ifPay.getResources().getDrawable(R.drawable.bg_bt_two));
        }

        Glide.with(context)
                .load(serviceInfos.get(position).getImage_path())
                .into(holder.service_image);
        holder.service_title.setText(serviceInfos.get(position).getName());
        holder.service_price.setText(String.valueOf(serviceInfos.get(position).getPrice()));
        holder.profile.setText(String.valueOf(serviceInfos.get(position).getDescription()));

        holder.ifPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count[position]){
                    count[position] = false;
                    serviceInfos.get(position).setIfPay(count[position]);
                    serviceActivity.updateMap(
                            serviceInfos,
                            position,
                            count[position]
                    );
                }else {
                    count[position] = true;
                    serviceInfos.get(position).setIfPay(count[position]);
                    serviceActivity.updateMap(
                            serviceInfos,
                            position,
                            count[position]
                    );
                }
                notifyItemChanged(position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowShoppingDialog showShoppingDialog = new ShowShoppingDialog();
                showShoppingDialog.showDialog(
                        context,
                        serviceInfos.get(position).getImage_path(),
                        serviceInfos.get(position).getName(),
                        serviceInfos.get(position).getPrice(),
                        serviceInfos.get(position).getDescription()
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceInfos.size();
    }

    public void initCount(Boolean[] newCount){
        // 初始化 count 数组
       count = newCount;
    }

    public void refreshData(ArrayList<ServiceInfo> infos){
        // 更新服务数据
        serviceInfos = infos;
        notifyDataSetChanged();
    }

    public class ServiceHolder extends RecyclerView.ViewHolder {
        TextView service_title, service_price, profile, ifPay;
        ImageView service_image;
        public ServiceHolder(View itemView) {
            super(itemView);
            service_title = itemView.findViewById(R.id.service_title);
            service_price = itemView.findViewById(R.id.service_price);
            profile = itemView.findViewById(R.id.profile);
            ifPay = itemView.findViewById(R.id.ifPay);
            service_image = itemView.findViewById(R.id.service_image);
        }
    }
}
