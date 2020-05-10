package ru.krestyankin.autoservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.krestyankin.autoservice.models.Order;
import ru.krestyankin.autoservice.models.OrderItem;
import ru.krestyankin.autoservice.models.SparePart;

import java.math.BigDecimal;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public Order calcTotal(Order order) throws Exception {
        logger.info("Заказ "+order.getId()+". Вычисляем итоговую сумму со скидкой ");
        Thread.sleep(100);
        order.setTotal(order.getItems().stream().map(x -> x.getQty().multiply(x.getPrice())).reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setTotalWithDiscount(order.getItems().stream().map(x -> x.getQty().multiply(x.getPrice())
                .multiply(BigDecimal.valueOf(100)
                        .subtract((x instanceof SparePart)?order.getCustomer().getPartsDiscount():order.getCustomer().getServicesDiscount()))
                .divide(BigDecimal.valueOf(100))).reduce(BigDecimal.ZERO, BigDecimal::add));
        return order;
    }
}
