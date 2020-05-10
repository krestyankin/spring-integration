package ru.krestyankin.autoservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Customer {
    private String name;
    private BigDecimal servicesDiscount;
    private BigDecimal partsDiscount;
}
