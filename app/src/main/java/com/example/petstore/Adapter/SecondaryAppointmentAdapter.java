package com.example.petstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petstore.R;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class SecondaryAppointmentAdapter extends RecyclerView.Adapter<SecondaryAppointmentAdapter.SecondaryAppointmentHolder> {
    private Context context;
    private List<String> week;
    private List<String> day;
    private int clickPosition = 0;
    private String date;
    private OnDateSelectedListener onDateSelectedListener;

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.onDateSelectedListener = listener;
    }


    public interface OnDateSelectedListener {
        void onDateSelected(String date);
    }


    public SecondaryAppointmentAdapter(Context context, Map<String, List<String>> weekDates) {
        this.context = context;
        this.week = weekDates.get("week");
        this.day = weekDates.get("day");


    }

    @NonNull
    @Override
    public SecondaryAppointmentAdapter.SecondaryAppointmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.secondary_appointment_item, parent, false);
        return new SecondaryAppointmentAdapter.SecondaryAppointmentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SecondaryAppointmentAdapter.SecondaryAppointmentHolder holder, int position) {
        // 设置日期和星期
        holder.day.setText(day.get(position));
        holder.week.setText(
                position == 0 ? "今天" :
                        position == 1 ? "明天" :
                                week.get(position)
        );

        // 根据点击状态更新背景颜色
        if (position == clickPosition) {
            holder.bg.setBackgroundResource(R.color.orange); // 选中项的颜色

            date = day.get(position);

            // 回调选中的日期
            if (onDateSelectedListener != null) {
                onDateSelectedListener.onDateSelected(date);
            }

        } else {
            holder.bg.setBackgroundResource(R.color.white); // 未选中项的颜色
        }

        // 点击事件监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更新点击状态
                int lastClick = clickPosition;
                clickPosition = position;

                // 通知 RecyclerView 刷新选中状态
                notifyItemChanged(lastClick); // 刷新上次选中的项

                notifyItemChanged(clickPosition); // 刷新当前选中的项
            }
        });
    }


    @Override
    public int getItemCount() {
        return day.size();
    }

    public static class SecondaryAppointmentHolder extends RecyclerView.ViewHolder {
        TextView week, day;
        View bg;


        public SecondaryAppointmentHolder(@NonNull View itemView) {
            super(itemView);
            week = itemView.findViewById(R.id.week);
            day = itemView.findViewById(R.id.day);
            bg = itemView.findViewById(R.id.bg);
        }
    }
}
