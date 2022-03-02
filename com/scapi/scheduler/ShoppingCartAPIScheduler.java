package com.scapi.scheduler;

import com.scapi.service.CartService;
//import com.scapi.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ShoppingCartAPIScheduler {

    Logger logger = LoggerFactory.getLogger(ShoppingCartAPIScheduler.class);

//    @Autowired
//    ProductService productService;
//
//    @Scheduled(fixedDelay = 60000)
//    //second, minute, hour, day of month, month, day(s) of week
//    //@Scheduled(cron = "0 15 * * * *")
//    public void checkForNewProducts(){
//        logger.info("Entry ::: checkForNewProducts Scheduler Call");
//        productService.addProductsFromFile();
//        logger.info("Exit ::: checkForNewProducts Scheduler Call");
//    }
}
