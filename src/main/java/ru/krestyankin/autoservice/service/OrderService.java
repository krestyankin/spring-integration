package ru.krestyankin.autoservice.service;

import ru.krestyankin.autoservice.models.Order;
import ru.krestyankin.autoservice.models.SparePart;

public interface OrderService {
    Order calcTotal(Order order) throws Exception;
}
