//package com.scapi.controller;
//
//import com.scapi.model.ProdAPIResponse;
//import com.scapi.entity.Product;
//import com.scapi.service.ProductService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//
//@RestController
//@RequestMapping(path = "/scapi/v1/product", produces = MediaType.APPLICATION_JSON_VALUE)
//public class ProductController {
//    Logger logger = LoggerFactory.getLogger(ProductController.class);
//
//    @Autowired
//    ProductService productService;
//
//    //To Add Product Details
//    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ProdAPIResponse> addProduct(@RequestBody Product product){
//        return productService.addProduct(product);
//    }
//
//    //To Add Product Details
//    @PostMapping(path = "/addByList", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ProdAPIResponse> addProductDetails(@RequestBody List<Product> productList){
//        return productService.addProductDetails(productList);
//    }
//
//    //To Get Product by Id
//    @GetMapping(path = "/get/{id}")
//    public ResponseEntity<ProdAPIResponse> getProductDetailsById(@PathVariable("id") int id){
//        return productService.getProductDetailsById(id);
//    }
//
//    //To Get product by List of Id(s)
//    @PostMapping(path = "/getByIdList")
//    public ResponseEntity<ProdAPIResponse> addProductDetailsByIdList(@RequestBody List<Integer> IdList){
//        return productService.getProductDetailsByIdList(IdList);
//    }
//
//    //To Get Product Details By Name
//    @GetMapping(path = "/getByName/{name}")
//    public ResponseEntity<ProdAPIResponse> getProductDetailsByName(@PathVariable("name") String productName){
//        return productService.getProductDetailsByName(productName);
//    }
//
//    //To Get All Product Details
//    @GetMapping(path = "/getAll")
//    public ResponseEntity<ProdAPIResponse> getAllProducts(){
//        return productService.getAllProducts();
//    }
//
//    //To Update Product Details by multiple id(s)
//    @PutMapping(path = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ProdAPIResponse> updateDetails(@RequestBody Product product){
//        return productService.updateProductDetails(product);
//    }
//
//    //To Delete Product Details By multiple Id(s)
//    @DeleteMapping(path="/deleteByIdList")
//    public ResponseEntity<ProdAPIResponse> deleteProductDetails(@RequestBody List<Integer> idList){
//        return productService.deleteproductDetails(idList);
//    }
//
//    //To Delete All products
//    @DeleteMapping(path = "/deleteAll")
//    public ResponseEntity<ProdAPIResponse> deleteAllProducts(){
//        return productService.deleteAllproducts();
//    }
//
//}
