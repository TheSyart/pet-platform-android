package com.example.petstore.Enum;

public enum OrderStatus {
    PENDING_CONFIRMATION(0, "订单待确认"),
    AWAITING_DELIVERY(1, "商品待配送"),
    SELF_PICKUP(2, "商品自取中"),
    PENDING_SERVICE(3, "服务待服务"),
    IN_TRANSIT(4, "商品配送中"),
    ARRIVED(5, "商品已到货"),
    CANCELLING(6, "订单取消中"),
    SERVICE_TIMEOUT(7, "服务已超时"),
    RETURN_REQUESTED(8, "申请退货中"),
    REFUNDED(9, "订单已退款"),
    COMPLETED(10, "订单已完成");

    private final int code;
    private final String description;

    // 构造方法
    OrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 获取代码
    public int getCode() {
        return code;
    }

    // 获取描述
    public String getDescription() {
        return description;
    }

    // 根据 code 获取枚举
    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未找到对应的订单状态: " + code);
    }
}
