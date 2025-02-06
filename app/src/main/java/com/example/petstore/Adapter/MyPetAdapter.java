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
import com.example.petstore.pojo.MyPet;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class MyPetAdapter extends RecyclerView.Adapter<MyPetAdapter.MyPetHolder> {
    private ArrayList<MyPet> myPetArrayList;
    private Context context;
    private OnPetSelectedListener listener;

    public MyPetAdapter(Context context, ArrayList<MyPet> myPetArrayList, OnPetSelectedListener listener) {
        this.context = context;
        this.myPetArrayList = myPetArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyPetAdapter.MyPetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mypet_item, parent, false);
        return new MyPetAdapter.MyPetHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull MyPetAdapter.MyPetHolder holder, int position) {
        Glide.with(context)
                .load(myPetArrayList.get(position).getPetImagePath())
                .into(holder.pet_image);

        holder.pet_name.setText(myPetArrayList.get(position).getPetName());
        holder.pet_age.setText(myPetArrayList.get(position).getPetBirth());
        holder.pet_sex.setText(myPetArrayList.get(position).getPetSex());
        holder.pet_breed.setText(myPetArrayList.get(position).getPetBreed());
        holder.pet_weight.setText(myPetArrayList.get(position).getPetWeight());
        holder.pet_coat.setText(myPetArrayList.get(position).getPetCoat());
        holder.pet_details.setText(myPetArrayList.get(position).getPetDetails());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPetSelected(myPetArrayList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return myPetArrayList.size();
    }

    public class MyPetHolder extends RecyclerView.ViewHolder {
        TextView pet_name, pet_age, pet_sex, pet_breed, pet_weight, pet_coat, pet_details;
        ImageView pet_image, modify;


        public MyPetHolder(View itemView) {
            super(itemView);
            pet_name = itemView.findViewById(R.id.pet_name);
            pet_age = itemView.findViewById(R.id.pet_age);
            pet_sex = itemView.findViewById(R.id.pet_sex);
            pet_breed = itemView.findViewById(R.id.pet_breed);
            pet_weight = itemView.findViewById(R.id.pet_weight);
            pet_coat = itemView.findViewById(R.id.pet_coat);
            pet_details = itemView.findViewById(R.id.pet_details);

            pet_image = itemView.findViewById(R.id.pet_image);

            modify = itemView.findViewById(R.id.modify);
        }
    }

    public interface OnPetSelectedListener {
        void onPetSelected(MyPet myPet);
    }


}
