package com.example.petstore.pojo;

public class Address {
    private String id;
    //用户ID
    private String customer_id;
    //收件人姓名
    private String name;
    //性别
    private Integer gender;
    //收件人联系电话
    private String phone;
    //地址名
    private String addressTitle;
    //地址详情
    private String addressDetails;
    //是否为默认地址
    private Integer defaultAddress;
    //经度
    private Double longitude;
    //纬度
    private Double latitude;
    //纬度
    private String door;

    // 无参构造方法
    public Address() {
        // 可以根据需要为属性提供默认值
    }

    // 有参构造方法
    public Address(String id, Integer defaultAddress, String name, Integer gender, String phone,
                   String addressTitle, String addressDetails, String door, Double longitude, Double latitude) {
        this.id = id;
        this.defaultAddress = defaultAddress;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.addressTitle = addressTitle;
        this.addressDetails = addressDetails;
        this.door = door;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id='" + id + '\'' +
                ", customer_id=" + customer_id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", addressTitle='" + addressTitle + '\'' +
                ", addressDetails='" + addressDetails + '\'' +
                ", defaultAddress='" + defaultAddress + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", door='" + door + '\'' +
                '}';
    }

    public Integer getDefaultAddress() {
        return defaultAddress;
    }
    public void setDefaultAddress(Integer defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getCustomer_id() {
        return customer_id;
    }
    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getAddressTitle() {
        return addressTitle;
    }
    public void setAddressTitle(String addressTitle) {
        this.addressTitle = addressTitle;
    }
    public String getAddressDetails() {
        return addressDetails;
    }
    public void setAddressDetails(String addressDetails) {
        this.addressDetails = addressDetails;
    }
    public Integer getGender() {
        return gender;
    }
    public void setGender(Integer gender) {
        this.gender = gender;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public String getDoor() {
        return door;
    }
    public void setDoor(String door) {
        this.door = door;
    }

}
