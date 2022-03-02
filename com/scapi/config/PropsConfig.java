package com.scapi.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@Data
public class PropsConfig {

    @Value("${scapi.cart.discount.percentage:5}")
    private int discountValue;

//    @Value("${scapi.product.file.path}")
//    private String prodFilePath;

    @Value("${scapi.product.getURL}")
    private String productGetURL;

    @Value("${scapi.product.updateURL}")
    private String productUpdateURL;
}
