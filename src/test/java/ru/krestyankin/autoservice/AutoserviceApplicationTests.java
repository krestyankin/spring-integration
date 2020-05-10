package ru.krestyankin.autoservice;

import org.apache.commons.lang3.RandomUtils;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import ru.krestyankin.autoservice.integration.Autoservice;
import ru.krestyankin.autoservice.models.Customer;
import ru.krestyankin.autoservice.models.Order;
import ru.krestyankin.autoservice.models.Serv;
import ru.krestyankin.autoservice.models.SparePart;
import ru.krestyankin.autoservice.service.PartService;
import ru.krestyankin.autoservice.service.ServService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AutoserviceApplicationTests {

	@Autowired
	private ApplicationContext context;

	@MockBean
	private PartService partService;

	@MockBean
	private ServService servService;

	@Test
	void shouldInvokeServicesAndCalcTotals() throws Exception {
		Autoservice autoservice = context.getBean(Autoservice.class);

		Order order1=new Order();
		order1.setCustomer(new Customer("Test", new BigDecimal(5), new BigDecimal(10)));
		SparePart part1 = new SparePart("test1");
		SparePart part2 = new SparePart("test2");
		Serv serv = new Serv();
		order1.setItems(List.of(part1, part2, serv));
		List<Order> orders = List.of(order1);
		autoservice.process(orders);
		Mockito.verify(partService, Mockito.times(2)).book(Mockito.any());
		Mockito.verify(servService, Mockito.times(1)).book(Mockito.any());
		assertThat(orders).hasSize(1);
		assertThat(orders.get(0).getTotalWithDiscount()).isNotNull();
	}

}
