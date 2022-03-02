package com.scapi.controller;

import com.scapi.model.CartAPIResponse;
import com.scapi.entity.Cart;
import com.scapi.model.ErrorDetails;
import com.scapi.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.scapi.constants.ShoppingCartAPIConstants.*;

@RestController
@RequestMapping(path = "/scapi/v1/cart", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value="Cart Management", protocols = "http")
public class CartController {

    Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    CartService cartService;

    //To Process Cart and adding it to database
    @ApiOperation(value = "To add Cart Details By passing Product list and Customer id", response = CartAPIResponse.class,code = 200)
    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CartAPIResponse> addCartDetails(@RequestBody Cart cart){
        try{
            return cartService.addCartDetails(cart);
        }
        catch(Exception ex){
            CartAPIResponse response = new CartAPIResponse();
            response.setOperation(OP_ADD);
            response.setStatus(-1);
            response.setError(new ErrorDetails(500, ex.getMessage()));
            return new ResponseEntity<CartAPIResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "To Get Cart Details By passing Cart id", response = CartAPIResponse.class,code = 200)
    @GetMapping(path = "/get/{id}")
    public ResponseEntity<CartAPIResponse> getCart(@PathVariable ("id") int id){
        return cartService.getCart(id);
    }

    //To Update Cart and adding it to database
    @ApiOperation(value = "To Update Cart Details By passing Product Details and Customer id", response = CartAPIResponse.class,code = 200)
    @PutMapping(path = "/update")
    public ResponseEntity<CartAPIResponse> updateCartDetails( @RequestBody Cart cart){
        return cartService.updateCartDetails(cart);
    }

//    //To Get All Cart Details
//    @GetMapping(path = "/getAll")
//    public ResponseEntity<CartAPIResponse> getAllCart(){
//        return cartService.getAllCart();
//    }

    //To Delete Cart
    @ApiOperation(value = "To Delete Cart By passing Cart id", response = CartAPIResponse.class,code = 200)
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<CartAPIResponse> deleteCartDetails(@PathVariable ("id") int id){
        return cartService.deleteCartDetails(id);
    }
}
