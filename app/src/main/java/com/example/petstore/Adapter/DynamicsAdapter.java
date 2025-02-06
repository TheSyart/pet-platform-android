package com.example.petstore.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.pojo.Dynamics;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.utils.StringUtils;
import org.json.JSONException;
import java.util.List;

public class DynamicsAdapter extends RecyclerView.Adapter<DynamicsAdapter.MyViewHolder> {
    private NetRequest netRequest;
    private Context mContext;
    private List<Dynamics> dynamicsArrayList;
    private String username;

    public DynamicsAdapter(Context context, List<Dynamics> dynamicsArrayList, String username) {
        this.mContext = context;
        this.dynamicsArrayList = dynamicsArrayList;
        this.username = username;

        setNetResponse(mContext);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dynamics_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // 先重置所有视图的状态
        holder.imageView.setVisibility(View.GONE);
        holder.imageView.setImageDrawable(null); // 重置图像

        // 移除旧的 OnCheckedChangeListener 防止复用带来的问题
        holder.likeButton.setOnCheckedChangeListener(null);

        //判断用户是否已经点过赞
        holder.likeButton.setChecked(dynamicsArrayList.get(position).getLike());

        holder.name.setText(dynamicsArrayList.get(position).getName());
        holder.time.setText(dynamicsArrayList.get(position).getSendtime());
        holder.content.setText(dynamicsArrayList.get(position).getContent());
        holder.likeNum.setText(String.valueOf(dynamicsArrayList.get(position).getLikeCount()));
        holder.likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 点赞逻辑
                    int newLikeCount = dynamicsArrayList.get(position).getLikeCount() + 1;
                    dynamicsArrayList.get(position).setLikeCount(newLikeCount);

                    holder.likeNum.setText(String.valueOf(newLikeCount));

                    // 更新后端数据
                    netRequest.getUpdateDynamicsRequest("updateNumAndPeople", dynamicsArrayList.get(position).getId(), username, 0);


                } else {
                    // 取消点赞逻辑
                    int newLikeCount = dynamicsArrayList.get(position).getLikeCount() - 1;
                    dynamicsArrayList.get(position).setLikeCount(newLikeCount);

                    holder.likeNum.setText(String.valueOf(newLikeCount));

                    // 更新后端数据
                    netRequest.getUpdateDynamicsRequest("updateNumAndPeople",dynamicsArrayList.get(position).getId(), username, 1);
                }

            }
        });

        //用户头像
        Glide.with(mContext)
                .load(dynamicsArrayList.get(position).getImage())
                .into(holder.user_image);

        //动态图片
        if (!StringUtils.isEmpty(dynamicsArrayList.get(position).getImage_path())){
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(dynamicsArrayList.get(position).getImage_path())
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return dynamicsArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView content;
        ImageView imageView;
        CheckBox likeButton;
        TextView likeNum;
        ImageView user_image;


        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            time = view.findViewById(R.id.time);
            content = view.findViewById(R.id.content);
            imageView = view.findViewById(R.id.content_image);
            likeButton = view.findViewById(R.id.like_button);
            likeNum = view.findViewById(R.id.like_num);
            user_image = view.findViewById(R.id.user_image);
        }
    }


    //接受网络请求返回数据
    private void setNetResponse(Context context) {
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                //更新点赞
                if (requestType.equals("getUpdateDynamicsRequest")) {
                    Toast.makeText(context, "点赞状态更改成功!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("getUpdateDynamicsRequest")){
                    Toast.makeText(context, "动态更新失败: " + errorMessage, Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
}