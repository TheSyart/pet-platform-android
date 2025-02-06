package com.example.petstore.dialogUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.utils.JwtUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class ShowCalendarDialog {
    private AlertDialog dialog;
    //五个控件
    private CalendarView calendarView;
    private Button deleteEvent;
    private Button editEvent;
    private Button saveEvent;
    private TextView date;
    private EditText event;

    //存储从服务器中获取的数据
    private ArrayList<String> calendarIdList = new ArrayList<>();
    private ArrayList<String> parentIdList = new ArrayList<>();
    private ArrayList<String> eventTimeList = new ArrayList<>();
    private ArrayList<String> eventContentList = new ArrayList<>();

    private String username;
    private NetRequest netRequest;
    private String jsonPayload;

    //当前日历选择的日期
    private String selectDate;
    public void showDialog(Context context) {
        // 获取username
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("loggedInUsername", null);



        //发送动态到服务器后返回数据
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            @Override
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("postPetCalendarRequest")) {
                    if (data.contains("添加")){

                        String[] parts = data.split("!");

                        String beforeExclamation = parts[0]; // 感叹号之前的部分
                        String afterExclamation = parts.length > 1 ? parts[1] : ""; // 感叹号之后的部分
                        calendarIdList.add(afterExclamation);
                        Log.e("ShowCalendarDialog", "添加事件后的新数据：" + calendarIdList + parentIdList + eventTimeList + eventContentList );
                        Toast.makeText(context, beforeExclamation, Toast.LENGTH_SHORT).show();
                    } else if (data.contains("更新")) {
                        Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
                    } else if (data.contains("删除")){
                        Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
                    } else{
                        // 将 data 转换为 JSONArray
                        JSONArray jsonResponse = new JSONArray(data);

                        // 遍历 JSONArray
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject jsonDynamic = jsonResponse.getJSONObject(i);
                            calendarIdList.add(jsonDynamic.getString("id"));
                            parentIdList.add(jsonDynamic.getString("parent_id"));
                            eventTimeList.add(jsonDynamic.getString("createDate"));
                            eventContentList.add(jsonDynamic.getString("event"));
                        }

                        System.out.println("今天日期是否已有事件：" + selectDate + "对比" + eventTimeList + eventTimeList.contains(selectDate));
                        if (eventTimeList.contains(selectDate)){
                            System.out.println("日期对比：" + eventTimeList +selectDate);
                            event.setText(eventContentList.get(eventTimeList.indexOf(selectDate)));
                        }

                    }


                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postPetCalendarRequest")) {
                    Toast.makeText(context, "GGGGGGGGGGGGGGGGG", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 创建一个AlertDialog.Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 获取布局解析器
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.calendar_petevent_dialog, null);

        // 初始化dialog内控件
        calendarView = dialogView.findViewById(R.id.calendarView);
        deleteEvent = dialogView.findViewById(R.id.deleteEvent);
        editEvent = dialogView.findViewById(R.id.editEvent);
        saveEvent = dialogView.findViewById(R.id.saveEvent);
        date = dialogView.findViewById(R.id.date);
        event = dialogView.findViewById(R.id.event);


        // 初始化并设置覆盖层的高度等于 CalendarView 的高度
        View calendarOverlay = dialogView.findViewById(R.id.calendarViewOverlay);
        calendarView.post(() -> calendarOverlay.getLayoutParams().height = calendarView.getHeight());
        calendarOverlay.setOnTouchListener((v, event1) -> {
            Toast.makeText(context, "点不了气死你", Toast.LENGTH_SHORT).show();
            return true;
        });

        //初始化显示今天日期
        selectDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        date.setText(selectDate);

        //向后端发送请求，获取该用户数据
        jsonPayload = "{\"username\":\"" + username + "\"}";
        netRequest.postPetCalendarRequest("getAllEvent", jsonPayload);

        //删除事件
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查事件是否已经创建
                if (eventTimeList.contains(selectDate)) {
                    int index = eventTimeList.indexOf(selectDate);

                    // 检查索引有效性后再执行删除操作
                    if (index >= 0 && index < calendarIdList.size()) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", calendarIdList.get(index));
                        } catch (JSONException e) {
                            Log.e("ShowCalendarDialog", "JSON 异常：" + e.getMessage());
                        } catch (IndexOutOfBoundsException e) {
                            Log.e("ShowCalendarDialog", "索引越界异常：" + e.getMessage());
                            throw e;
                        }

                        jsonPayload = jsonObject.toString();
                        netRequest.postPetCalendarRequest("deleteEvent", jsonPayload);

                        // 删除索引并清除相应的字段
                        calendarIdList.remove(index);
                        parentIdList.remove(index);
                        eventTimeList.remove(index);
                        eventContentList.remove(index);
                        event.setText("");
                        Log.e("ShowCalendarDialog", "删除事件后的新数据：" + calendarIdList + parentIdList + eventTimeList + eventContentList );

                        event.setEnabled(false);
                        deleteEvent.setVisibility(View.GONE);
                        editEvent.setVisibility(View.VISIBLE);
                        saveEvent.setEnabled(false);
                        calendarOverlay.setVisibility(View.GONE); // 显示遮罩层
                    } else {
                        Log.e("ShowCalendarDialog", "删除事件时索引无效：" + index);
                    }
                }else {
                    Toast.makeText(context, "无内容可删除!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //编辑事件
        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.setEnabled(true);
                deleteEvent.setVisibility(View.VISIBLE);
                editEvent.setVisibility(View.GONE);
                saveEvent.setEnabled(true);
                calendarOverlay.setVisibility(View.VISIBLE); // 显示遮罩层

            }
        });

        //保存事件
        saveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeText = date.getText().toString();
                String eventText = event.getText().toString();

                //已有该日期事件，只需变更内容
                if (eventTimeList.contains(timeText)){
                    int index = eventTimeList.indexOf(timeText);
                    //文本内容是否变化了
                    if (eventContentList.get(index).equals(eventText)){
                        Toast.makeText(context, "内容未变化，后端不用更新！", Toast.LENGTH_SHORT).show();
                    }else {
                        System.out.println("old time : " + timeText);
                        eventContentList.set(index, eventText);

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", calendarIdList.get(index));
                            jsonObject.put("event", eventContentList.get(index));  // 自动处理换行符
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        jsonPayload = jsonObject.toString();
                        netRequest.postPetCalendarRequest("updateEvent", jsonPayload);
                    }


                //没有创建过事件
                }else if (eventText.isEmpty()){
                    Toast.makeText(context, "未编辑文本，不用保存!", Toast.LENGTH_SHORT).show();


                    //没有该日期事件，创建新的
                }else {
                    System.out.println("new time : " + timeText);
                    parentIdList.add(parentIdList.get(0));
                    eventTimeList.add(timeText);
                    eventContentList.add(eventText);


                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("parent_id", parentIdList.get(0));
                        jsonObject.put("createDate", timeText);
                        jsonObject.put("event", eventText);  // 自动处理换行符
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("保存的事件String字符串 ：" + jsonObject);
                    jsonPayload = jsonObject.toString();

                    netRequest.postPetCalendarRequest("InsertNewEvent", jsonPayload);
                }

                deleteEvent.setVisibility(View.GONE);
                editEvent.setVisibility(View.VISIBLE);
                saveEvent.setEnabled(false);
                event.setEnabled(false);
                calendarOverlay.setVisibility(View.GONE); // 隐藏遮罩层

            }
        });

        //日历监视器
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                System.out.println("当前选中时间：" + selectDate);
                date.setText(selectDate);
                if (eventTimeList.contains(selectDate)){
                    event.setText(eventContentList.get(eventTimeList.indexOf(selectDate)));
                }else {
                    event.setText("");
                }

            }
        });


        // 将自定义布局设置到对话框中
        builder.setView(dialogView);

        // 创建AlertDialog对象并显示
        dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));//去除边框
        dialog.show();

    }
}
