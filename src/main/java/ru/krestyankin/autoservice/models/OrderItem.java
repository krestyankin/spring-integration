package ru.krestyankin.autoservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItem {
    protected String name;
    protected BigDecimal price;
    protected BigDecimal qty;
    protected String status;

    public OrderItem() {
        qty=BigDecimal.ONE;
        price=BigDecimal.ZERO;
        status="OK";
    }
}
