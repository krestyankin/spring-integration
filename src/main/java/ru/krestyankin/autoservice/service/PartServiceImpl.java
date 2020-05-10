package ru.krestyankin.autoservice.service;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.krestyankin.autoservice.models.SparePart;

import java.math.BigDecimal;

@Service("partService")
public class PartServiceImpl implements PartService {
    private static final Logger logger = LoggerFactory.getLogger(PartServiceImpl.class);

    @Override
    public void book(SparePart part) throws Exception {
        logger.info("Заказ запчасти "+part.getPartNumber());
        Thread.sleep(100 + RandomUtils.nextInt(0, 100));
        part.setPrice(BigDecimal.valueOf(10+ RandomUtils.nextInt(10, 20)));
        logger.info("Запчасть "+part.getPartNumber()+" заказана");
    }
}
