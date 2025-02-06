package com.example.petstore.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecentWeekDates {

    public Map<String, List<String>> getRecentWeekDates(){
        // 获取当前日期
        LocalDate today = LocalDate.now();

        // 存储最近一周的日期
        Map<String, List<String>> data = new HashMap<>();
        List<String> recentWeekDates = new ArrayList<>();
        List<String> dayOfWeekDates = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i); // 当前日期加 i 天
            DayOfWeek dayOfWeek = date.getDayOfWeek(); // 获取星期几
            dayOfWeekDates.add(getChineseDayOfWeek(dayOfWeek));
            recentWeekDates.add(String.valueOf(date));
        }
        data.put("day", recentWeekDates);
        data.put("week", dayOfWeekDates);

        return data;
    }

    // 将英文星期转换为中文星期
    private static String getChineseDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "星期一";
            case TUESDAY:
                return "星期二";
            case WEDNESDAY:
                return "星期三";
            case THURSDAY:
                return "星期四";
            case FRIDAY:
                return "星期五";
            case SATURDAY:
                return "星期六";
            case SUNDAY:
                return "星期日";
            default:
                return "";
        }
    }

    public Boolean TimeSlotChecker(LocalTime endTime) {
        // 当前时间
        LocalTime now = LocalTime.now().plusHours(8);
        System.out.println("now-------------------->" + now);
        System.out.println("endDateTime-------------------->" + endTime);

        if (now.isAfter(endTime)) {
            return false;
        } else {
           return true;
        }
    }


    public Boolean AppointmentChecker(String appointmentTime, String serviceTimeSlot) {
        if (StringUtils.isEmpty(appointmentTime) || StringUtils.isEmpty(serviceTimeSlot)){
            return false;
        }
        // 当前时间
        LocalDateTime now = LocalDateTime.now();


        // 解析日期和时间段
        LocalDate date = LocalDate.parse(appointmentTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String[] timeSlots = serviceTimeSlot.split("-");
        LocalTime startTime = LocalTime.parse(timeSlots[0], DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTime = LocalTime.parse(timeSlots[1], DateTimeFormatter.ofPattern("HH:mm"));

        // 构造完整的时间范围
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        System.out.println("now-------------------->" + now);
        System.out.println("endDateTime-------------------->" + endDateTime);
        // 检查是否超过
        if (now.isAfter(endDateTime)) {
            return false;
        } else {
            return true;
        }

    }

}
