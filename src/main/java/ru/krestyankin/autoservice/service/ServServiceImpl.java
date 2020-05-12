package ru.krestyankin.autoservice.service;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.krestyankin.autoservice.models.Serv;

import java.math.BigDecimal;

@Service("servService")
public class ServServiceImpl implements ServService {
    private static final Logger logger = LoggerFactory.getLogger(ServServiceImpl.class);

    @Override
    public void book(Serv serv) throws Exception {
        logger.info("Бронирование ресурса "+ serv.getName());
        Thread.sleep(100 + RandomUtils.nextInt(0, 100));
        if(RandomUtils.nextInt(0, 3)==0) {
            logger.error("Ошибка бронирования ресурса "+ serv.getName());
            throw new RuntimeException("Not found");
        }
        serv.setPrice(BigDecimal.valueOf(5 + RandomUtils.nextInt(1, 10)));
        logger.info("Бронирование ресурса "+ serv.getName()+" завершено");
    }

}
