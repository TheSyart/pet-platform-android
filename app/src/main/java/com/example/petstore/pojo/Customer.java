package com.example.petstore.pojo;

// 创建一个请求体类，包含要发送的字段
public  class Customer {
    private String phone;
    private String gender;
    private String code;


//    public Customer(String phone, String password, String gender) {
//        this.phone = phone;
//        this.password = password;
//        this.gender = gender;
//    }

    // Getter 和 Setter 方法（如果需要）
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
