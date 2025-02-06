package com.example.petstore.pojo;

public class ServiceInfo {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer shoppingTypeId;
    private String image_path;
    private Boolean ifPay;

    // 构造方法
    public ServiceInfo(Long id, String name, String description, Double price, Integer shoppingTypeId, String image_path, Boolean ifPay) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.shoppingTypeId = shoppingTypeId;
        this.image_path = image_path;
        this.ifPay = ifPay;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getShoppingTypeId() {
        return shoppingTypeId;
    }

    public void setShoppingTypeId(Integer shoppingTypeId) {
        this.shoppingTypeId = shoppingTypeId;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
    public Boolean getIfPay() {
        return ifPay;
    }

    public void setIfPay(Boolean ifPay) {
        this.ifPay = ifPay;
    }

    @Override
    public String toString() {
        return "ServiceInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", shoppingTypeId=" + shoppingTypeId +
                ", image_path='" + image_path + '\'' +
                ", ifPay='" + ifPay + '\'' +
                '}';
    }

}
