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
import com.example.petstore.activity.EncyclopediaActivity;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.dialogUtils.ShowPetDetailsDialog;
import com.example.petstore.pojo.Encyclopedia;
import com.example.petstore.pojo.EncyclopediaSpecies;
import com.example.petstore.utils.JwtUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class EncyclopediaAdapter extends RecyclerView.Adapter<EncyclopediaAdapter.EncyclopediaHolder> {
    private NetRequest netRequest;
    private ShowPetDetailsDialog showPetDetailsDialog = new ShowPetDetailsDialog();
    private List<EncyclopediaSpecies> showSpeciesList;
    private List<EncyclopediaSpecies> showPetsList = new ArrayList<>();
    private List<EncyclopediaSpecies> nowShowList;
    private Context context;
    private String type = "species";
    private EncyclopediaActivity.OnItemClickListener onItemClickListener;

    public EncyclopediaAdapter(Context context, List<EncyclopediaSpecies> showSymbolList, EncyclopediaActivity.OnItemClickListener listener) {
        this.context = context;
        this.showSpeciesList = showSymbolList;
        nowShowList = new ArrayList<>(showSymbolList); // 拷贝一份，避免引用问题
        this.onItemClickListener = listener;
        setNetResponse();
    }


    @NonNull
    @Override
    public EncyclopediaAdapter.EncyclopediaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.encyclopedia_petspecies_item, parent, false);
        return new EncyclopediaAdapter.EncyclopediaHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull EncyclopediaAdapter.EncyclopediaHolder holder, int position) {
        Glide.with(context)
                .load(nowShowList.get(position).getImage())
                .into(holder.petSpecies_image);
        holder.petSpecies_name.setText(nowShowList.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击宠物分类
                if (type.equals("species")){

                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(); // 触发回调
                    }

                    updateType("pets");
                    netRequest.getEncyclopediaRequest("oneSpeciesDetails", nowShowList.get(position).getId());

                //点击具体宠物
                } else if (type.equals("pets")) {
                    netRequest.getPetDetailsRequest("onePetDetails", nowShowList.get(position).getId());
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return nowShowList.size();
    }


    public class EncyclopediaHolder extends RecyclerView.ViewHolder {
        TextView petSpecies_name;
        ImageView petSpecies_image;

        public EncyclopediaHolder(View itemView) {
            super(itemView);
            petSpecies_image = itemView.findViewById(R.id.petSpecies_image);
            petSpecies_name = itemView.findViewById(R.id.petSpecies_name);

        }
    }

    public void showBackHome(){
        nowShowList = new ArrayList<>(showSpeciesList);
        notifyDataSetChanged();
    }
    public void updateType(String newType){
        type = newType;
    }

    //接受网络请求返回数据
    private void setNetResponse() {
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                //获取宠物百科分类图标与类名
                if (requestType.equals("getEncyclopediaRequest")) {
                    showPetsList.clear();
                    nowShowList.clear();
                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);
                    // 遍历 JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject json = jsonResponse.getJSONObject(i);
                        EncyclopediaSpecies encyclopediaSpecies =
                                new EncyclopediaSpecies(
                                        json.getString("id"),
                                        json.getString("name"),
                                        json.getString("image")
                                );
                        showPetsList.add(encyclopediaSpecies);
                    }
                    nowShowList = showPetsList;
                    notifyDataSetChanged();
                }
                if (requestType.equals("getPetDetailsRequest")) {

                    // 将 data 转换为 jsonObject
                    JSONObject json = new JSONObject(data); // 解析单个 JSON 对象

                    //宠物具体数据
                    Encyclopedia petDetails = new Encyclopedia(
                            json.getString("id"),
                            json.getString("parentId"),
                            json.getString("petName"),
                            json.getString("content"),
                            json.getString("petWeight"),
                            json.getString("petHeight"),
                            json.getString("petOrigin"),
                            json.getString("petLife"),
                            json.getString("petShape"),
                            json.getString("anotherName"),
                            json.getString("petPrice"),
                            json.getString("image")
                    );
                    showPetDetailsDialog.showDialog(context, petDetails);
                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("getEncyclopediaRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("getPetDetailsRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

