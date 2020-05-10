package ru.krestyankin.autoservice.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.krestyankin.autoservice.models.Order;
import java.util.List;

@MessagingGateway
public interface Autoservice {
    @Gateway(requestChannel = "ordersInputChannel")
    List<Order> process(List<Order> orders);
}
