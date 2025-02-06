package com.example.petstore.Adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petstore.R;
import com.example.petstore.pojo.TimeSlot;
import com.example.petstore.utils.RecentWeekDates;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TimeslotSecondaryAdapter extends RecyclerView.Adapter<TimeslotSecondaryAdapter.TimeslotSecondaryHolder> {
    private RecentWeekDates recentWeekDates = new RecentWeekDates();
    private Context context;
    private int clickPosition = -1;
    private String currentDay;
    private List<TimeSlot> timeSlotList;
    private OnDateSelectedListener onDateSelectedListener;

    public void setOnDateSelectedListener(TimeslotSecondaryAdapter.OnDateSelectedListener listener) {
        this.onDateSelectedListener = listener;
    }

    public interface OnDateSelectedListener {
        void onDateSelected(Integer date);
    }

    public TimeslotSecondaryAdapter(Context context, List<TimeSlot> timeSlotList, String currentDay) {
        this.context = context;
        this.currentDay = currentDay;
        this.timeSlotList = timeSlotList;
    }

    @NonNull
    @Override
    public TimeslotSecondaryAdapter.TimeslotSecondaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.secondary_timeslot__item, parent, false);
        return new TimeslotSecondaryAdapter.TimeslotSecondaryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeslotSecondaryAdapter.TimeslotSecondaryHolder holder, int position) {
        holder.timeslot.setText(timeSlotList.get(position).getTimeSlot());

        LocalDate currentDate = LocalDate.parse(currentDay);
        LocalDate now = LocalDate.now();

        // 检查当前日期是否为今天
        if (currentDate.equals(now)) {
            // 检验该时段是否已经过去
            if (recentWeekDates.TimeSlotChecker(timeSlotList.get(position).getEndTime())) {
                // 判断该时段是否已预约满
                if (timeSlotList.get(position).getCapacity() > timeSlotList.get(position).getNowPeople()) {
//                    holder.itemView.setEnabled(true); // 启用
                    holder.bg.setBackgroundResource(position == clickPosition ? R.color.orange : R.color.white);
                    holder.itemView.setTag("available"); // 标记为可用
                } else {
//                    holder.itemView.setEnabled(false); // 禁用
                    holder.itemView.setTag("full"); // 标记为已满
                    holder.bg.setBackgroundResource(R.color.grey); // 背景为灰色
                }
            } else {
//                holder.itemView.setEnabled(false); // 已过期，禁用
                holder.bg.setBackgroundResource(R.color.grey);
                holder.itemView.setTag("expired"); // 标记为过期
            }
        } else { // 日期晚于今天
            // 判断该时段是否已预约满
            if (timeSlotList.get(position).getCapacity() > timeSlotList.get(position).getNowPeople()) {
//                holder.itemView.setEnabled(true); // 启用
                holder.bg.setBackgroundResource(position == clickPosition ? R.color.orange : R.color.white);
                holder.itemView.setTag("available"); // 标记为可用
            } else {
//                holder.itemView.setEnabled(false); // 禁用
                holder.itemView.setTag("full"); // 标记为已满
                holder.bg.setBackgroundResource(R.color.grey); // 背景为灰色
            }
        }

        // 如果是选中项，回调选中的日期
        if (position == clickPosition && holder.itemView.isEnabled()) {
            if (onDateSelectedListener != null) {
                onDateSelectedListener.onDateSelected(timeSlotList.get(position).getTimeslotId());
            }
        }

        // 点击事件监听，用于更新选中状态
        holder.itemView.setOnClickListener(v -> {
            // 检查是否已满
            if ("full".equals(holder.itemView.getTag())) {
                Toast.makeText(v.getContext(), "该时段已预约满", Toast.LENGTH_SHORT).show();
            } else if ("expired".equals(holder.itemView.getTag())) {
                Toast.makeText(v.getContext(), "该时段已过期", Toast.LENGTH_SHORT).show();
            } else {
                // 更新点击状态
                int lastClick = clickPosition;
                clickPosition = position;

                // 通知 RecyclerView 刷新选中状态
                if (lastClick != -1){
                    notifyItemChanged(lastClick); // 刷新上次选中的项
                }
                notifyItemChanged(clickPosition); // 刷新当前选中的项
            }
        });
    }


    @Override
    public int getItemCount() {
        return timeSlotList.size();
    }

    public static class TimeslotSecondaryHolder extends RecyclerView.ViewHolder {
        TextView timeslot;
        View bg;


        public TimeslotSecondaryHolder(@NonNull View itemView) {
            super(itemView);
            timeslot = itemView.findViewById(R.id.timeslot);
            bg = itemView.findViewById(R.id.bg);
        }
    }
}