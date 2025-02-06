package com.example.petstore.pojo;

import java.math.BigDecimal;

public class OrderInfo {

    private Integer id;
    //商品名称
    private String name;
    //商品单价
    private Double price;
    //商品数量
    private Integer quantity;
    //商品小计
    private BigDecimal subtotal;
    private String image_path;

    // 构造方法
    public OrderInfo(Integer id, String name, Integer quantity, Double price, BigDecimal subtotal, String image_path) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
        this.image_path = image_path;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", subtotal=" + subtotal +
                ", image_path='" + image_path + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
