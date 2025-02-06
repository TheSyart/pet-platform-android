package com.example.petstore.pojo;
public class MyPet {
    private String id;
    private String petName;
    private String petBreed;
    private String petSex;
    private String petWeight;
    private String petBirth;
    private String petCoat;
    private String petDetails;
    private String parentId;
    private String petImagePath;

    // 构造方法
    public MyPet(String id, String petName, String petBreed, String petSex,
                 String petWeight, String petBirth, String petCoat,
                 String petDetails, String parentId, String petImagePath) {
        this.id = id;
        this.petName = petName;
        this.petBreed = petBreed;
        this.petSex = petSex;
        this.petWeight = petWeight;
        this.petBirth = petBirth;
        this.petCoat = petCoat;
        this.petDetails = petDetails;
        this.parentId = parentId;
        this.petImagePath = petImagePath;
    }

    // Getters 和 Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetBreed() {
        return petBreed;
    }

    public void setPetBreed(String petBreed) {
        this.petBreed = petBreed;
    }

    public String getPetSex() {
        return petSex;
    }

    public void setPetSex(String petSex) {
        this.petSex = petSex;
    }

    public String getPetWeight() {
        return petWeight;
    }

    public void setPetWeight(String petWeight) {
        this.petWeight = petWeight;
    }

    public String getPetBirth() {
        return petBirth;
    }

    public void setPetBirth(String petBirth) {
        this.petBirth = petBirth;
    }

    public String getPetCoat() {
        return petCoat;
    }

    public void setPetCoat(String petCoat) {
        this.petCoat = petCoat;
    }

    public String getPetDetails() {
        return petDetails;
    }

    public void setPetDetails(String petDetails) {
        this.petDetails = petDetails;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPetImagePath() {
        return petImagePath;
    }

    public void setPetImagePath(String petImagePath) {
        this.petImagePath = petImagePath;
    }

    @Override
    public String toString() {
        return "MyPet{" +
                "id='" + id + '\'' +
                ", petName='" + petName + '\'' +
                ", petBreed='" + petBreed + '\'' +
                ", petSex='" + petSex + '\'' +
                ", petWeight='" + petWeight + '\'' +
                ", petBirth='" + petBirth + '\'' +
                ", petCoat='" + petCoat + '\'' +
                ", petDetails='" + petDetails + '\'' +
                ", parentId='" + parentId + '\'' +
                ", petImagePath='" + petImagePath + '\'' +
                '}';
    }
}


