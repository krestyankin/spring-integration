package ru.krestyankin.autoservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SparePart extends OrderItem {
    private String partNumber;

    @Override
    public String toString() {
        return "SparePart{" +
                "partNumber='" + partNumber + "', " +
                "price=" + price +
                '}';
    }
}
