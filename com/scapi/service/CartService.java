package com.scapi.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.scapi.DAO.CartDAO;
import com.scapi.DAO.CustomerDAO;
//import com.scapi.DAO.ProductDAO;
import com.scapi.config.PropsConfig;
import com.scapi.entity.Customer;
import com.scapi.model.CartAPIResponse;
import com.scapi.entity.Cart;
import com.scapi.model.ErrorDetails;
import com.scapi.entity.Product;
import static com.scapi.constants.ShoppingCartAPIConstants.*;

import org.apache.tomcat.util.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.RollbackException;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {

    Logger logger = LoggerFactory.getLogger(CartService.class);

//    @Value("${scapi.cart.discount.percentage:5}")
//    private int discountValue;

    //@Autowired
    //ProductDAO productDAO;

    @Autowired
    CartDAO cartDAO;

    @Autowired
    CustomerDAO customerDAO;

    @Autowired
    PropsConfig config;

    @Autowired
    ProductEndPointAPIService endPointAPIService;

    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseEntity<CartAPIResponse> addCartDetails(Cart cart) {
        CartAPIResponse response = new CartAPIResponse();
        response.setStatus(0);
        response.setOperation(OP_ADD);
        double totalAmount = 0;
        try{
            if(cart.getCustomer() != null && cart.getCustomer().getCustId() > 0){
                if(!customerDAO.existsById(cart.getCustomer().getCustId())){
                    throw new EntityNotFoundException("Customer not found");
                }
            }
            String[] productWithQty = cart.getProductList().split(TILT);  // "PID1#2~PID2#1~PID3#1"
            if(productWithQty.length < 1)
            {
                throw new ValidationException("Product List is Mandatory");
            }
            Map<Integer, Integer> idQty = new HashMap<>();                         // "PID1#2", "PID2#1", "PID3#1"
            for (String i : productWithQty) {
                String[] prodIDQty = i.split(HASH);                      // [PID1 2] [PID2 1]  [PID3 1]
                idQty.put(Integer.parseInt(prodIDQty[0].substring(3)), Integer.parseInt(prodIDQty[1])); // [1 2] [2 1] [3 1]
            }
            //List<Product> prodList = productDAO.getByIdCollection(idQty.keySet().stream().collect(Collectors.toList()));
            List<Product> prodList = endPointAPIService.getProductList(idQty.keySet().stream().collect(Collectors.toList()));
            List<Integer> idList = idQty.keySet().stream().collect(Collectors.toList());
            List<Integer> idNF = idList.stream().filter(id -> prodList.stream().noneMatch(prod -> prod.getProductId() == id)).collect(Collectors.toList());
            if(idNF.size() > 0){
                throw new EntityNotFoundException("Product Id(s) not found");
            }
            logger.info("Product List" + prodList);
            for (Map.Entry<Integer, Integer> inst : idQty.entrySet()) {      //eg. 1. [1 2] 2. [2 1] 3.[3 1]
                Product product = prodList.stream().filter(prod -> prod.getProductId() == inst.getKey()).findFirst().get();
                logger.info("Product" + product);
                if (product.getProductName() != null) {
                    product.setProductSoldCount(product.getProductSoldCount() + inst.getValue());
                    double prodAmount = product.getProductAmount() * inst.getValue();
                    totalAmount = totalAmount + prodAmount;
                }
            }
            LocalDate today = LocalDate.now();
            cart.setOrderDate(today);
            logger.info("Total Amount before Discount ::: "+ totalAmount);
            totalAmount = totalAmount - (totalAmount * (config.getDiscountValue() * .01));
            logger.info("Total Amount after Discount (" + config.getDiscountValue() + "%) ::: "+ totalAmount);
            cart.setCartAmount(totalAmount);
            // Step 1
            cartDAO.save(cart);
            // Step 2
            endPointAPIService.updateProductList(prodList);
            response.setCart(cart);
            return new ResponseEntity<CartAPIResponse>(response, HttpStatus.OK);
        }
        catch (NumberFormatException | EntityNotFoundException ex) {
            response.setStatus(-1);
            response.setError(new ErrorDetails(100, "Record Not Found. Please Check Product ID/Customer Details"));
            return new ResponseEntity<CartAPIResponse>(response, HttpStatus.NOT_FOUND);
        }
        catch (ValidationException ex) {
            response.setStatus(-1);
            response.setError(new ErrorDetails(100, "Product List is Mandatory"));
            return new ResponseEntity<CartAPIResponse>(response, HttpStatus.NOT_FOUND);
        }
        catch (Exception ex) {
            if(ex instanceof RuntimeException){
                logger.info("Runtime Exception occured");
                throw new RuntimeException(ex.getMessage());
            }
            else{
                ex.printStackTrace();
                response.setStatus(-1);
                response.setError(new ErrorDetails(500, ex.getMessage()));
                return new ResponseEntity<CartAPIResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    public ResponseEntity<CartAPIResponse> updateCartDetails( Cart cart) {
        CartAPIResponse response = new CartAPIResponse();
        //int id = cart.getCartId();              //eg. cart id 1
        Map<Integer, Integer> oldProdMap = new HashMap<>();
        Map<Integer, Integer> resultMap = new HashMap<>();
        double totalPrice = 0;
        try {
            Cart oldCart = cartDAO.getOne(cart.getCartId());
            String oldProdList = oldCart.getProductList();         // eg. "PID1#2~PID2#2~PID3#1
            String[] oldProdListArray = oldProdList.split(TILT);
            // Loading Old Cart Prod Map
            for (String i : oldProdListArray) {
                String[] prodIDQty = i.split(HASH);       // Map -> (Key 1,value 2), (Key 2, Value 2), (Key 3, value 1)
                oldProdMap.put(Integer.parseInt(prodIDQty[0].substring(3)), Integer.parseInt(prodIDQty[1]));
                resultMap.put(Integer.parseInt(prodIDQty[0].substring(3)), 0); //result map -> (key 1, value 0) (key 2, value 0),(key 3, value 0),(key 4 , value 0)
            }
        } catch (NumberFormatException | EntityNotFoundException e) {
            response.setStatus(-1);
            response.setError(new ErrorDetails(100, "Record Not Found. Please Input correct cart id"));
            return new ResponseEntity<CartAPIResponse>(response, HttpStatus.NOT_FOUND);
        }
        Map<Integer, Integer> newProdMap = new HashMap<>();
        String newProdList = cart.getProductList();
        String[] newProdListArray = newProdList.split(TILT); //eg. "PID2#1~PID3#2~PID4#3
        // Loading New Cart Prod Map
        for (String i : newProdListArray) {
            String[] prodIDQty = i.split(HASH);                // Map -> (Key  2, Value 1), (Key 3 , Value 2), (Key 4,value 3)
            newProdMap.put(Integer.parseInt(prodIDQty[0].substring(3)), Integer.parseInt(prodIDQty[1]));
            resultMap.put(Integer.parseInt(prodIDQty[0].substring(3)), 0);  // result map -> (key 2, value 0),(key 3, value 0),(key 4 , value 0)
        }

        // Loading result Map
        for (Map.Entry<Integer, Integer> inst : resultMap.entrySet()) {
            int key = inst.getKey();
            if (newProdMap.containsKey(key) && (oldProdMap.containsKey(key))) {
                resultMap.put(key, (newProdMap.get(key) - oldProdMap.get(key)));   // (2 , (1-2)) = (2,-1) , ( 3, (2 -1)) = (3,1)
            } else if (oldProdMap.containsKey(key)) {
                resultMap.put(key, -oldProdMap.get(key));  //   ( 1, (0 - 2)) = (1,-2)
            } else {
                resultMap.put(key, newProdMap.get(key));    // Result Map -> (key 1 value -2),(key 2 value -1),(key 3 value 1)
            }                                                                                   // (Key 4 value 3)
            logger.info("Result Map    " + resultMap);
        }
//            Old Code
//            List<Product> productList = productDAO.getByIdCollection(resultMap.keySet().stream().collect(Collectors.toList()));
//            logger.info("Product List   " + productList);
//            for(Map.Entry<Integer, Integer> inst : resultMap.entrySet()){
//                Product product = productList.stream().filter(prod -> prod.getProductId() == inst.getKey()).findFirst().get();
//                if(product.getProductName() != null){
//                    product.setProductSoldCount(product.getProductSoldCount() + inst.getValue());
//                }
//            }
//            // Calculate Total Amount of cart
//            List<Product> newProd = productDAO.getByIdCollection(newProdMap.keySet().stream().collect(Collectors.toList()));
//            for(Map.Entry<Integer, Integer>  inst : newProdMap.entrySet()){
//                Product product = newProd.stream().filter(prod -> prod.getProductId() == inst.getKey()).findFirst().get();
//                if(product.getProductName() != null){
//                    double prodAmount  = product.getProductAmount() * inst.getValue();
//                    totalPrice = totalPrice + prodAmount;
//                }
//
//            }
        // Product Sold Count and Total Value Updation
        try {
            //List<Product> prodList = productDAO.getByIdCollection(resultMap.keySet().stream().collect(Collectors.toList()));
            List<Product> prodList = endPointAPIService.getProductList(resultMap.keySet().stream().collect(Collectors.toList()));
            List<Integer> idList = resultMap.keySet().stream().collect(Collectors.toList());
            List<Integer> idNF = idList.stream().filter(ids -> prodList.stream().noneMatch(prod -> prod.getProductId() == ids)).collect(Collectors.toList());
            if(idNF.size() > 0){
                throw new EntityNotFoundException();
            }
            for (Product prod : prodList) {

                // To update Sold Prod Count

                prod.setProductSoldCount(prod.getProductSoldCount() + resultMap.get(prod.getProductId()));

                // To calculate Total Price

                if (newProdMap.containsKey(prod.getProductId())) {
                    totalPrice = totalPrice + (prod.getProductAmount() * newProdMap.get(prod.getProductId()));
                }
            }

            //response.setProductList(prodList);
            cart.setCartAmount(totalPrice);
            LocalDate today = LocalDate.now();
            cart.setOrderDate(today);
            cartDAO.save(cart);
            endPointAPIService.updateProductList(prodList);
            response.setOperation(OP_UPD);
            response.setCart(cart);
            return new ResponseEntity<CartAPIResponse>(response, HttpStatus.OK);

        }
        catch (EntityNotFoundException ex) {
            response.setStatus(-1);
            response.setError(new ErrorDetails(100, "Record Not Found. Please Check Product ID Details"));
            return new ResponseEntity<CartAPIResponse>(response, HttpStatus.NOT_FOUND);

        }
        catch (Exception ex) {
            response.setStatus(-1);
            response.setError(new ErrorDetails(500, ex.getMessage()));
            return new ResponseEntity<CartAPIResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }


//        Set<Map.Entry<Integer,Integer>> filter = dbCart.entrySet();
//        for(Map.Entry<Integer,Integer> entry : newProd.entrySet()){
//            if(!filter.contains(entry)){
//                result.put(entry.getKey(), entry.getValue());
//        }
//        for(Map.Entry<Integer,Integer> inst : dbCart.entrySet()){
//            for (Map.Entry<Integer,Integer> inst1 : newProd.entrySet()){
//                if(!(inst.getKey() == inst1.getKey())){
//                    result.put(inst1.getKey(), inst1.getValue());  //result map -> (key 1, value 2)
//                }                                                  // map -> (key 4, value 3)
//                else{
//                    result.put(inst1.getKey(),(inst1.getValue()-inst.getValue())); // result map -> (key 2, value (2 -1) = 1)
//                }                                                                  // map -> (key 3 , value (1 -2) = -1
//            }
//        }

    }

    public ResponseEntity<CartAPIResponse> deleteCartDetails(int id) {
        CartAPIResponse response = new CartAPIResponse();
        response.setStatus(0);
        response.setOperation(OP_REM);
        try {
            Cart cart = cartDAO.getOne(id);
            String prodList = cart.getProductList();
            Map<Integer, Integer> prodMap = new HashMap<>();
            String[] prodListArray = prodList.split(TILT);
            for (String i : prodListArray) {
                String[] prodIDQty = i.split(HASH);
                prodMap.put(Integer.parseInt(prodIDQty[0].substring(3)), Integer.parseInt(prodIDQty[1]));
            }
            //List<Product> productList = productDAO.getByIdCollection(prodMap.keySet().stream().collect(Collectors.toList()));
            List<Product> productList = endPointAPIService.getProductList(prodMap.keySet().stream().collect(Collectors.toList()));
            List<Integer> idList = prodMap.keySet().stream().collect(Collectors.toList());
            List<Integer> idNF = idList.stream().filter(ids -> productList.stream().noneMatch(prod -> prod.getProductId() == ids)).collect(Collectors.toList());
            if(idNF.size() > 0){
                throw new EntityNotFoundException();
            }
            for (Product prod : productList) {
                prod.setProductSoldCount(prod.getProductSoldCount() - prodMap.get(prod.getProductId()));
            }
            cartDAO.delete(cart);
            endPointAPIService.updateProductList(productList);
        } catch (EmptyResultDataAccessException  | EntityNotFoundException ex) {
            response.setStatus(-1);
            response.setError(new ErrorDetails(100, "Invalid Records Found. Please Check your Product Id"));
            return new ResponseEntity<CartAPIResponse>(response, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            response.setStatus(-1);
            response.setError(new ErrorDetails(500, ex.getMessage()));
            return new ResponseEntity<CartAPIResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<CartAPIResponse>(response, HttpStatus.OK);

    }

    public ResponseEntity<CartAPIResponse> getCart(int id) {
        CartAPIResponse response = new CartAPIResponse();
        response.setStatus(0);
        response.setOperation(OP_GET);
        try {
            Cart cart = cartDAO.getOne(id);
            if(cart.getProductList() != null) {
                response.setCart(cart);
            }
            response.setStatus(0);
            return new ResponseEntity<CartAPIResponse>(response, HttpStatus.OK);
        }
        catch ( EntityNotFoundException ex){
            response.setStatus(-1);
            response.setError(new ErrorDetails(100, "Invalid Cart Id"));
            return new ResponseEntity<CartAPIResponse>(response, HttpStatus.NOT_FOUND);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(-1);
            response.setError(new ErrorDetails(500, ex.getMessage()));
            return new ResponseEntity<CartAPIResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}