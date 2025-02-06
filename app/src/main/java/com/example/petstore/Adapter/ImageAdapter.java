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
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<String> imageList;
    private List<String> nameList;
    private Context context;

    public ImageAdapter(Context context, List<String> imageList, List<String> nameList) {
        this.context = context;
        this.imageList = imageList;
        this.nameList = nameList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewpage_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Glide.with(context)
                .load(imageList.get(position))
                .into(holder.pet_image);
        holder.pet_name.setText(nameList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView pet_image;
        TextView pet_name;

        public ImageViewHolder(View itemView) {
            super(itemView);
            pet_image = itemView.findViewById(R.id.pet_image);
            pet_name = itemView.findViewById(R.id.pet_name);
        }
    }
}

