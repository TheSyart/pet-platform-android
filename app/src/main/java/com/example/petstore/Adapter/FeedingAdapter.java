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
import com.example.petstore.dao.NetRequest;
import com.example.petstore.pojo.FeedingSkill;
import com.example.petstore.utils.JwtUtils;
import org.json.JSONException;
import java.util.List;

public class FeedingAdapter extends RecyclerView.Adapter<FeedingAdapter.FeedingHolder> {
    private NetRequest netRequest;
    private  List<FeedingSkill> feedingSkillArrayList;
    private Context context;

    public FeedingAdapter(Context context, List<FeedingSkill> feedingSkillArrayList) {
        this.context = context;
        this.feedingSkillArrayList = feedingSkillArrayList;
        setNetResponse();
    }


    @NonNull
    @Override
    public FeedingAdapter.FeedingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.encyclopedia_feedingskill_item, parent, false);
        return new FeedingAdapter.FeedingHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull FeedingAdapter.FeedingHolder holder, int position) {
        Glide.with(context)
                .load(feedingSkillArrayList.get(position).getImage_path())
                .into(holder.image);
        holder.title.setText(feedingSkillArrayList.get(position).getTitle());
        holder.content.setText(feedingSkillArrayList.get(position).getContent());
        holder.createDate.setText(feedingSkillArrayList.get(position).getCreateDate());


    }

    @Override
    public int getItemCount() {
        return feedingSkillArrayList.size();
    }


    public class FeedingHolder extends RecyclerView.ViewHolder {
        TextView title, content, createDate;
        ImageView image;

        public FeedingHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            createDate = itemView.findViewById(R.id.createDate);

        }
    }

    //接受网络请求返回数据
    private void setNetResponse() {
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
//                //获取宠物百科分类图标与类名
//                if (requestType.equals("getEncyclopediaRequest")) {
//                    showPetsList.clear();
//                    nowShowList.clear();
//                    // 将 data 转换为 JSONArray
//                    JSONArray jsonResponse = new JSONArray(data);
//                    // 遍历 JSONArray
//                    for (int i = 0; i < jsonResponse.length(); i++) {
//                        JSONObject json = jsonResponse.getJSONObject(i);
//                        EncyclopediaSpecies encyclopediaSpecies =
//                                new EncyclopediaSpecies(
//                                        json.getString("id"),
//                                        json.getString("name"),
//                                        json.getString("image_path")
//                                );
//                        showPetsList.add(encyclopediaSpecies);
//                    }
//                    nowShowList = showPetsList;
//                    notifyDataSetChanged();
//                }
//                if (requestType.equals("getPetDetailsRequest")) {
//
//                    // 将 data 转换为 jsonObject
//                    JSONObject json = new JSONObject(data); // 解析单个 JSON 对象
//
//                    //宠物具体数据
//                    Encyclopedia petDetails = new Encyclopedia(
//                            json.getString("id"),
//                            json.getString("parentId"),
//                            json.getString("petName"),
//                            json.getString("content"),
//                            json.getString("petWeight"),
//                            json.getString("petHeight"),
//                            json.getString("petOrigin"),
//                            json.getString("petLife"),
//                            json.getString("petShape"),
//                            json.getString("anotherName"),
//                            json.getString("petPrice"),
//                            json.getString("image_path")
//                    );
//                    showPetDetailsDialog.showDialog(context, petDetails);
//                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
//                if (requestType.equals("getEncyclopediaRequest")) {
//                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
//                }
//                if (requestType.equals("getPetDetailsRequest")) {
//                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }
}


