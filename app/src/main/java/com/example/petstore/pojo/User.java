package com.example.petstore.pojo;

public class User {
    private String id;
    private String name;
    private String username;
    private String password;
    private String birth;
    private String phone;
    private Integer gender;
    private String image;
    private String code;

    public User(){

    }

    public User(String id, String name, String username, String password, String birth, String phone, Integer gender, String image) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.birth = birth;
        this.phone = phone;
        this.gender = gender;
        this.image = image;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", birth='" + birth + '\'' +
                ", phone='" + phone + '\'' +
                ", gender=" + gender +
                ", image='" + image + '\'' +
                '}';
    }
}
