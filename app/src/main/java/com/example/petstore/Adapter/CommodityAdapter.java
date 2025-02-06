package com.example.petstore.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petstore.R;
import com.example.petstore.activity.ShoppingActivity;
import com.example.petstore.dialogUtils.ShowShoppingDialog;
import com.example.petstore.pojo.Commodity;
import com.example.petstore.utils.PurchaseButton;

import java.math.BigDecimal;
import java.util.List;

public class CommodityAdapter extends RecyclerView.Adapter<CommodityAdapter.CommodityHolder> {
    private List<Commodity> commodityList;
    private ShoppingActivity shoppingActivity;
    private Context context;
    private int[] count;
    private int totalNum = 0;

    public CommodityAdapter(ShoppingActivity shoppingActivity, Context context, List<Commodity> commodityList) {
        this.shoppingActivity = shoppingActivity;
        this.context = context;
        this.commodityList = commodityList;
    }

    @NonNull
    @Override
    public CommodityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.commodity_item, parent, false);
        return new CommodityHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull CommodityHolder holder, int position) {
        holder.shopping_title.setText(commodityList.get(position).getName());
        holder.shopping_price.setText(String.valueOf(commodityList.get(position).getPrice()));
        holder.profile.setText(commodityList.get(position).getDescription());
        holder.stock.setText(commodityList.get(position).getStock() == 0 ? "商品告罄" : String.valueOf(commodityList.get(position).getStock()));
        Glide.with(context)
                .load(commodityList.get(position).getImageUrl())
                .into(holder.shopping_image);



        // 检查商品是否告罄
        if (commodityList.get(position).getStock() == 0) {
            holder.overlay_image.setVisibility(View.VISIBLE); // 显示覆盖图
            holder.purchaseButton.setVisibility(View.GONE); // 禁用按钮

        } else {
            holder.overlay_image.setVisibility(View.INVISIBLE); // 隐藏覆盖图
            holder.purchaseButton.setVisibility(View.VISIBLE); // 启用按钮

        }

        // 设置按钮的初始状态
        if (count[position] > 0) {
            holder.purchaseButton.setTextNum(count[position]);
        } else {
            holder.purchaseButton.resetView();
        }

        // 添加按钮点击事件
        holder.purchaseButton.setOnShoppingClickListener(new PurchaseButton.ShoppingClickListener() {
            @Override
            public void onfirst(int num) {
                count[position] = 1;
                shoppingActivity.updateComponent(calculateTotalPrice(), totalNum);
            }

            @Override
            public void onAddClick(int num) {
                if (count[position] < commodityList.get(position).getStock()){
                    count[position]++;
                    shoppingActivity.updateComponent(calculateTotalPrice(), totalNum);
                } else {
                    Toast.makeText(context, "商品已无库存", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onMinusClick(int num) {
                if (count[position] > 0) {
                    count[position]--;
                    shoppingActivity.updateComponent(calculateTotalPrice(), totalNum);
                } else {
                    Toast.makeText(context, "不能减了", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.shopping_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowShoppingDialog showShoppingDialog = new ShowShoppingDialog();
                showShoppingDialog.showDialog(
                        context,
                        commodityList.get(position).getImageUrl(),
                        commodityList.get(position).getName(),
                        commodityList.get(position).getPrice(),
                        commodityList.get(position).getDescription()
                        );
            }
        });
    }


    @Override
    public int getItemCount() {
        if (commodityList != null && !commodityList.isEmpty() && count == null) {
            count = new int[commodityList.size()]; // 初始化 count 数组
        }
        return commodityList.size();
    }

    public boolean isGroupHeader(int position) {
        if (position == 0) {
            return true;
        } else {
            String currentGroupName = getGroupName(position);
            String lastGroupName = getGroupName(position - 1);
            boolean isHeader = !currentGroupName.equals(lastGroupName);
            return isHeader;
        }
    }

    public String getGroupName(int position) {
        return commodityList.get(position).getCategory();
    }

    public BigDecimal calculateTotalPrice() {
        BigDecimal sum = BigDecimal.ZERO;
        totalNum = 0;

        for (int i = 0; i < count.length; i++) {
            // 将每个商品价格和数量转换为 BigDecimal
            BigDecimal price = BigDecimal.valueOf(commodityList.get(i).getPrice());
            BigDecimal quantity = BigDecimal.valueOf(count[i]);

            // 计算单个商品的总价并累加
            sum = sum.add(price.multiply(quantity));

            // 累加数量
            totalNum += count[i];
        }
        return sum;
    }

    public int[] getCommodityCount() {
        return count;
    }

    public class CommodityHolder extends RecyclerView.ViewHolder {
        TextView shopping_title;
        TextView shopping_price;
        TextView profile;
        TextView stock;
        ImageView shopping_image, overlay_image;
        PurchaseButton purchaseButton;

        public CommodityHolder(View itemView) {
            super(itemView);
            shopping_title = itemView.findViewById(R.id.shopping_title);
            shopping_price = itemView.findViewById(R.id.shopping_price);
            profile = itemView.findViewById(R.id.profile);
            stock = itemView.findViewById(R.id.stock);
            shopping_image = itemView.findViewById(R.id.shopping_image);
            purchaseButton = itemView.findViewById(R.id.purchaseButton);
//            purchaseButton.setTextNum(0);

            overlay_image = itemView.findViewById(R.id.overlay_image);
        }
    }
}
