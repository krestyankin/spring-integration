package ru.krestyankin.autoservice.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Order {
    private String id;
    private Customer customer;
    private List<OrderItem> items;
    private BigDecimal total;
    private BigDecimal totalWithDiscount;

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customer=" + customer +
                ", items=" + items +
                ", total=" + total +
                ", totalWithDiscount=" + totalWithDiscount +
                "}\n";
    }
}
