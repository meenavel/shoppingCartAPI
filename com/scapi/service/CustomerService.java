package com.scapi.service;

import com.scapi.DAO.CustomerDAO;
import com.scapi.entity.Cart;
import com.scapi.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class CustomerService {

    Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    CustomerDAO customerDAO;

    @Autowired
    CartService cartService;

    public ResponseEntity addCustomer(Customer customer) throws Exception{

        try {
            customerDAO.save(customer);
        }
        catch (TransactionSystemException ex){
           // ex.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        }
        catch (Exception ex){
                //ex.printStackTrace();
                 return new ResponseEntity(HttpStatus.BAD_REQUEST);

            }
        return new ResponseEntity(HttpStatus.OK);

    }


    public List<Customer> getCustomer() {
        return customerDAO.findAll();
    }

    @Transactional
    public ResponseEntity deleteCustomer(int id) {

        try {
            Customer customer = customerDAO.getOne(id);
            //logger.info("Customer" +customer);
            List<Cart> cartList = customer.getCartList();
            for (Cart cart : cartList) {
                cartService.deleteCartDetails(cart.getCartId());
            }
            customerDAO.deleteById(id);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        catch (Exception ex){
            //ex.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
