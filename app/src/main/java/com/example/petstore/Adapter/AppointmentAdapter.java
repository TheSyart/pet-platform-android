package com.example.petstore.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.pojo.Address;
import com.example.petstore.pojo.EmpDoctor;
import com.example.petstore.pojo.OrderInfo;
import com.example.petstore.pojo.TimeSlot;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.utils.RecentWeekDates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private NetRequest netRequest;
    private RecentWeekDates recentWeekDates = new RecentWeekDates();
    private List<EmpDoctor> empDoctorList;
    private Context context;
    private int clickPosition = -1;
    private List<TimeSlot> timeSlots = new ArrayList<>();

    //当前的选择，各项值
    private String currentDay;
    private Integer currentDoctorId;
    private Integer currentTimeslotId;

    public AppointmentAdapter(Context context, List<EmpDoctor> empDoctorList) {
        this.context = context;
        this.empDoctorList = empDoctorList;
    }

    @NonNull
    @Override
    public AppointmentAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.appointment_item, parent, false);
        return new AppointmentAdapter.AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.AppointmentViewHolder holder, int position) {
        // 取消旧的监听器，避免状态更新冲突
        holder.select_doctor.setOnCheckedChangeListener(null);

        // 根据点击状态更新
        if (position == clickPosition) {
            holder.select_doctor.setChecked(true);
            holder.appointmentSecondary_recycler.setVisibility(View.VISIBLE);
            holder.timeslotSecondary_recycler.setVisibility(View.VISIBLE);

            // 设置横向布局管理器
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.appointmentSecondary_recycler.setLayoutManager(layoutManager);

            SecondaryAppointmentAdapter secondaryAdapter = new SecondaryAppointmentAdapter(context, recentWeekDates.getRecentWeekDates());
            secondaryAdapter.setOnDateSelectedListener(new SecondaryAppointmentAdapter.OnDateSelectedListener() {
                @Override
                public void onDateSelected(String selectedDate) {
                    // 接收到 SecondaryAppointmentAdapter 传递的日期
                    Log.d("SelectedDate", "收到的日期是: " + selectedDate);

                    int doctorId = empDoctorList.get(position).getId();
                    currentDoctorId = doctorId;
                    currentDay =  selectedDate;

                    // 更新 TimeslotSecondaryAdapter 的数据或其他逻辑
                    getTimeSlot(doctorId, holder.timeslotSecondary_recycler, LocalDate.parse(selectedDate));
                }
            });
            holder.appointmentSecondary_recycler.setAdapter(secondaryAdapter);





        } else {
            holder.select_doctor.setChecked(false);
            holder.appointmentSecondary_recycler.setVisibility(View.GONE);
            holder.timeslotSecondary_recycler.setVisibility(View.GONE);
        }


        // 加载数据到视图
        Glide.with(context)
                .load(empDoctorList.get(position).getImage())
                .into(holder.doctor_image);
        holder.doctor_name.setText(empDoctorList.get(position).getName());
        holder.doctor_phone.setText(empDoctorList.get(position).getPhone());

        // 设置监听器
        holder.select_doctor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 更新点击状态
            int lastClick = clickPosition;
            clickPosition = position;


            if (lastClick == clickPosition){
                clickPosition = -1;
                notifyItemChanged(position); // 两次点击同一项
            } else {
                if (lastClick != -1){
                    notifyItemChanged(lastClick); // 刷新上次选中的项
                }
                notifyItemChanged(clickPosition); // 刷新当前选中的项
            }
        });
    }


    @Override
    public int getItemCount() {
        return empDoctorList.size();
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder {
        ImageView doctor_image;
        TextView doctor_phone, doctor_name;
        CheckBox select_doctor;
        RecyclerView appointmentSecondary_recycler, timeslotSecondary_recycler;


        public AppointmentViewHolder(View itemView) {
            super(itemView);
            doctor_image = itemView.findViewById(R.id.doctor_image);
            doctor_phone = itemView.findViewById(R.id.doctor_phone);
            doctor_name = itemView.findViewById(R.id.doctor_name);

            select_doctor = itemView.findViewById(R.id.select_doctor);

            appointmentSecondary_recycler = itemView.findViewById(R.id.appointmentSecondary_recycler);
            timeslotSecondary_recycler = itemView.findViewById(R.id.timeslotSecondary_recycler);

        }
    }

    private void getTimeSlot(Integer doctorId, RecyclerView timeslotRecycler, LocalDate today) {
        setNetResponse(context, timeslotRecycler); // 动态传递 RecyclerView

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("today", today);
            jsonObject.put("doctorId", doctorId);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        String jsonPayload = jsonObject.toString();
        netRequest.postServiceInfoRequest("queryTimeSlot", jsonPayload);

    }


    private void setNetResponse(Context context, RecyclerView timeslotRecycler) {
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            @Override
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("postServiceInfoRequest")) {
                    // 清空旧数据
                    timeSlots.clear();

                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);

                    // 遍历 JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject json = jsonResponse.getJSONObject(i);

                        // 使用构造方法直接创建 TimeSlot 对象
                        TimeSlot timeSlot = new TimeSlot(
                                json.getInt("id"),
                                json.getInt("timeslot_id"),
                                json.getString("time_slot"),
                                LocalTime.parse(json.getString("start_time")),
                                LocalTime.parse(json.getString("end_time")),
                                json.getInt("capacity"),
                                json.getInt("nowPeople")
                        );
                        timeSlots.add(timeSlot);
                    }

                    // 初始化并设置适配器
                    TimeslotSecondaryAdapter timeslotAdapter = new TimeslotSecondaryAdapter(context, timeSlots, currentDay);
                    timeslotAdapter.setOnDateSelectedListener(new TimeslotSecondaryAdapter.OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(Integer date) {
                            // 接收到 TimeslotSecondaryAdapter 传递的时段编号
                            Log.d("SelectedDate", "收到的时段编号是: " + date);
                            currentTimeslotId = date;
                        }
                    });
                    timeslotRecycler.setAdapter(timeslotAdapter);

                    // 设置 GridLayoutManager，参数 3 表示一行显示 3 列
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
                    timeslotRecycler.setLayoutManager(gridLayoutManager);

                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postServiceInfoRequest")) {
                    Toast.makeText(context, "数据加载失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getOrderInfo(int type){
        return (type == 0 ? currentDay :
                type == 1 ? String.valueOf(currentDoctorId) : String.valueOf(currentTimeslotId));
    }

}