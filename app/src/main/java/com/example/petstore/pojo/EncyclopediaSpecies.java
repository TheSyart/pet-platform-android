package com.example.petstore.pojo;

public class EncyclopediaSpecies {
    private String id;
    private String name;
    private String image;

    // 无参构造函数
    public EncyclopediaSpecies() {
    }

    // 全参构造函数
    public EncyclopediaSpecies(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    // Getter 和 Setter 方法
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "EncyclopediaSpecies{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}

