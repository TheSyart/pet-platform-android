package com.example.petstore.pojo;

import java.time.LocalTime;

public class TimeSlot {
    private Integer id; // 唯一标识
    private Integer timeslotId; // 时段ID
    private String timeSlot; // 时间段描述
    private LocalTime startTime; // 开始时间
    private LocalTime endTime; // 结束时间
    private Integer capacity; // 容量
    private Integer nowPeople; // 当前人数

    // 全参构造器
    public TimeSlot(Integer id, Integer timeslotId, String timeSlot, LocalTime startTime, LocalTime endTime, Integer capacity, Integer nowPeople) {
        this.id = id;
        this.timeslotId = timeslotId;
        this.timeSlot = timeSlot;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.nowPeople = nowPeople;
    }

    // Getter 和 Setter 方法（如需 Lombok，可用 @Data 注解代替）
    public Integer getId() {
        return id;
    }

    public Integer getTimeslotId() {
        return timeslotId;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Integer getNowPeople() {
        return nowPeople;
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "id=" + id +
                ", timeslotId=" + timeslotId +
                ", timeSlot='" + timeSlot + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", capacity=" + capacity +
                ", nowPeople=" + nowPeople +
                '}';
    }
}
