package com.example.petstore.pojo;

public class EmpDoctor {
    private Integer id;

    private String phone;

    private String name;

    private Integer gender;

    private String image;

    private Integer job;

    public EmpDoctor(Integer id, String phone, String name, Integer gender, String image, Integer job) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.gender = gender;
        this.image = image;
        this.job = job;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getJob() {
        return job;
    }

    public void setJob(Integer job) {
        this.job = job;
    }

    @Override
    public String toString() {
        return "EmpDoctor{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", image='" + image + '\'' +
                ", job=" + job +
                '}';
    }
}
