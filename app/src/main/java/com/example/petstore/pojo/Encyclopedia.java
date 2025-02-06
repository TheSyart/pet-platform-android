package com.example.petstore.pojo;

public class Encyclopedia {
    private String id;
    private String parentId;
    private String petName;
    private String content;
    private String petWeight;
    private String petHeight;
    private String petOrigin;
    private String petLife;
    private String petShape;
    private String anotherName;
    private String petPrice;
    private String petImage;

    // 构造函数
    public Encyclopedia(String id, String parentId, String petName, String content,
                           String petWeight, String petHeight, String petOrigin,
                           String petLife, String petShape, String anotherName,
                           String petPrice, String petImage) {
        this.id = id;
        this.parentId = parentId;
        this.petName = petName;
        this.content = content;
        this.petWeight = petWeight;
        this.petHeight = petHeight;
        this.petOrigin = petOrigin;
        this.petLife = petLife;
        this.petShape = petShape;
        this.anotherName = anotherName;
        this.petPrice = petPrice;
        this.petImage = petImage;
    }

    // Getter 和 Setter 方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPetWeight() {
        return petWeight;
    }

    public void setPetWeight(String petWeight) {
        this.petWeight = petWeight;
    }

    public String getPetHeight() {
        return petHeight;
    }

    public void setPetHeight(String petHeight) {
        this.petHeight = petHeight;
    }

    public String getPetOrigin() {
        return petOrigin;
    }

    public void setPetOrigin(String petOrigin) {
        this.petOrigin = petOrigin;
    }

    public String getPetLife() {
        return petLife;
    }

    public void setPetLife(String petLife) {
        this.petLife = petLife;
    }

    public String getPetShape() {
        return petShape;
    }

    public void setPetShape(String petShape) {
        this.petShape = petShape;
    }

    public String getAnotherName() {
        return anotherName;
    }

    public void setAnotherName(String anotherName) {
        this.anotherName = anotherName;
    }

    public String getPetPrice() {
        return petPrice;
    }

    public void setPetPrice(String petPrice) {
        this.petPrice = petPrice;
    }

    public String getPetImage() {
        return petImage;
    }

    public void setPetImage(String petImage) {
        this.petImage = petImage;
    }
}

