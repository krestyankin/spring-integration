package ru.krestyankin.autoservice;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.krestyankin.autoservice.integration.Autoservice;
import ru.krestyankin.autoservice.models.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class AutoserviceApplication {
	private static final Logger logger = LoggerFactory.getLogger(AutoserviceApplication.class);

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(AutoserviceApplication.class, args);

		Autoservice autoservice = context.getBean(Autoservice.class);

		List<Order> orders = List.of(generateOrder("1"), generateOrder("2"), generateOrder("3"));

		logger.info("Начало обработки");
		long startTime = System.nanoTime();
		autoservice.process(orders);
		long endTime   = System.nanoTime();
		logger.info("Обработка завершена за "+(endTime - startTime)/1000/1000+"мс");

		System.out.println(orders);

		System.exit(0);
	}

	private static Order generateOrder(String id){
		Order order=new Order();
		order.setId(id);
		Customer customer = new Customer("Client_"+id, new BigDecimal(RandomUtils.nextInt(5, 15)), new BigDecimal(RandomUtils.nextInt(5, 15)));
		order.setCustomer(customer);
		List<OrderItem> items = new ArrayList<>();
		for(int i=0; i<RandomUtils.nextInt(1, 5); i++) {
			SparePart part = new SparePart("PARTNUM"+id+"_"+i);
			part.setName("Part "+i);
			part.setQty(BigDecimal.ONE);
			items.add(part);
		}
		for(int i=0; i<RandomUtils.nextInt(1, 5); i++) {
			Serv serv = new Serv();
			serv.setName("Услуга "+id+"_"+i);
			items.add(serv);
		}
		order.setItems(items);
		return order;
	}
}
