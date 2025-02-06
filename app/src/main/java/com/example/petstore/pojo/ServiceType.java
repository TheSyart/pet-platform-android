package com.example.petstore.pojo;

import java.math.BigDecimal;

public class ServiceType {
    private Integer typeId;

    private String name;

    private String description;

    private String imagePath;

    // 构造方法
    public ServiceType(Integer typeId, String name, String description, String imagePath) {
        this.typeId = typeId;
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
    }
    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "ServiceType{" +
                "typeId=" + typeId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }

}
