package com.example.petstore.pojo;

public class Order {
    private String orderId;
    private String customerId;
    private int pickMethod;
    private double totalAmount;
    private int paymentMethod;
    private String note;
    private int orderStatus;
    private String createDate;
    private String reservedName;
    private String reservedPhone;
    private String secretKey;
    private String appointmentTime;
    private String doctorName; // 医生姓名
    private String doctorPhone; // 医生电话
    private String petName; // 宠物名称
    private String serviceTimeSlot; // 服务时间段

    // 更新后的构造方法
    public Order(String orderId, String customerId, int pickMethod, double totalAmount,
                 int paymentMethod, String note, int orderStatus, String createDate,
                 String reservedName, String reservedPhone, String secretKey, String appointmentTime,
                 String doctorName, String doctorPhone, String petName, String serviceTimeSlot) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.pickMethod = pickMethod;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.note = note;
        this.orderStatus = orderStatus;
        this.createDate = createDate;
        this.reservedName = reservedName;
        this.reservedPhone = reservedPhone;
        this.appointmentTime = appointmentTime;
        this.secretKey = secretKey;
        this.doctorName = doctorName;
        this.doctorPhone = doctorPhone;
        this.petName = petName;
        this.serviceTimeSlot = serviceTimeSlot;
    }

    // Getters and Setters

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getPickMethod() {
        return pickMethod;
    }

    public void setPickMethod(int pickMethod) {
        this.pickMethod = pickMethod;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getReservedName() {
        return reservedName;
    }

    public void setReservedName(String reservedName) {
        this.reservedName = reservedName;
    }

    public String getReservedPhone() {
        return reservedPhone;
    }

    public void setReservedPhone(String reservedPhone) {
        this.reservedPhone = reservedPhone;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }

    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getServiceTimeSlot() {
        return serviceTimeSlot;
    }

    public void setServiceTimeSlot(String serviceTimeSlot) {
        this.serviceTimeSlot = serviceTimeSlot;
    }

    // 更新后的 toString 方法
    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", pickMethod=" + pickMethod +
                ", totalAmount=" + totalAmount +
                ", paymentMethod=" + paymentMethod +
                ", note='" + note + '\'' +
                ", orderStatus=" + orderStatus +
                ", createDate='" + createDate + '\'' +
                ", reservedName='" + reservedName + '\'' +
                ", reservedPhone='" + reservedPhone + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", appointmentTime='" + appointmentTime + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", doctorPhone='" + doctorPhone + '\'' +
                ", petName='" + petName + '\'' +
                ", serviceTimeSlot='" + serviceTimeSlot + '\'' +
                '}';
    }
}
