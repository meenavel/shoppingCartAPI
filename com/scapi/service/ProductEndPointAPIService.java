package com.scapi.service;

import com.scapi.config.PropsConfig;
import com.scapi.entity.Product;
import com.scapi.model.ProdAPIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductEndPointAPIService {

    Logger logger = LoggerFactory.getLogger(ProductEndPointAPIService.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    PropsConfig config;

    private Map<String, String> headerMap = new HashMap<String, String>();

    @PostConstruct
    public void init(){
        headerMap.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    public List<Product> getProductList(List<Integer> productIdList) throws Exception {
            logger.info("Product URL ::: "+config.getProductGetURL());
            try{
                ResponseEntity<ProdAPIResponse> productResponse = restTemplate.postForEntity(
                        config.getProductGetURL(),
                        productIdList,
                        ProdAPIResponse.class,
                        headerMap);
                logger.info("Product API Response ::: "+productResponse.getBody().getProductList());
                return productResponse.getBody().getProductList();
            }
            catch(Exception ex){
                ex.printStackTrace();
                if(ex instanceof HttpClientErrorException){
                    if(((HttpClientErrorException) ex).getStatusCode().value() == 404){
                        throw new EntityNotFoundException("Product not found");
                    }
                }
                else if(ex instanceof ResourceAccessException){
                    throw new RuntimeException("Product Endpoint not available");
                }
                throw new RuntimeException("Technical exception occured ! Please try again later");
            }
    }

    public void updateProductList(List<Product> productList) throws Exception {
        logger.info("Product URL ::: "+config.getProductUpdateURL());
        try{
            if(productList.size() == 2){
                throw new RuntimeException();
            }
            ResponseEntity<ProdAPIResponse> productResponse = restTemplate.postForEntity(
                    config.getProductUpdateURL(),
                    productList,
                    ProdAPIResponse.class,
                    headerMap);
            logger.info("Product API Response ::: "+productResponse.getStatusCodeValue());
        }
        catch(Exception ex){
            ex.printStackTrace();
            if(ex instanceof HttpClientErrorException){
                if(((HttpClientErrorException) ex).getStatusCode().value() == 404){
                    throw new EntityNotFoundException("Product not found");
                }
            }
            else if(ex instanceof ResourceAccessException){
                throw new RuntimeException("Product Endpoint not available");
            }
            throw new RuntimeException("Technical exception occured ! Please try again later");
        }

    }
}
