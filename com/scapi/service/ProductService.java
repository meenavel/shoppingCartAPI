//package com.scapi.service;
//
//import com.scapi.DAO.ProductDAO;
//import com.scapi.config.PropsConfig;
//import com.scapi.model.ErrorDetails;
//import com.scapi.model.ProdAPIResponse;
//import com.scapi.entity.Product;
//
//
//import static com.scapi.constants.ShoppingCartAPIConstants.*;
//
//
//import javax.annotation.PostConstruct;
//import javax.validation.ConstraintViolation;
//import javax.validation.ConstraintViolationException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.TransactionSystemException;
//
//import javax.persistence.EntityNotFoundException;
//import javax.persistence.RollbackException;
//import java.io.File;
//import java.nio.file.Files;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class ProductService {
//
//    Logger logger = LoggerFactory.getLogger(ProductService.class);
//
//    @Autowired
//    ProductDAO productDAO;
//
//    @Autowired
//    PropsConfig config;
//
//    //@PostConstruct
//    public void addProductsFromFile(){
//        List<Product> fileProdList = new ArrayList<>();
//        try{
//            File file = new File(config.getProdFilePath());
//            String content = new String(Files.readAllBytes(file.toPath()));
//            String [] contentArr = content.split(System.getProperty(LINE_PATH));
//            for(String prod : contentArr){
//                String [] prodArr = prod.split(COMMA);
//                Product product = new Product();
//                product.setProductName(prodArr[0]);
//                product.setProductDescription(prodArr[1]);
//                product.setProductAmount(Double.parseDouble(prodArr[2]));
//                product.setProductSoldCount(Integer.parseInt(prodArr[3]));
//                fileProdList.add(product);
//            }
//            List<Product> dbProdList = productDAO.findAll();
//            List<Product> addProdList = fileProdList
//                    .stream()
//                    .filter(p1 -> dbProdList.stream().noneMatch(p2 -> p2.getProductName().equals(p1.getProductName())))
//                    .collect(Collectors.toList());
//            logger.info("New Product(s) to be added count ::: "+addProdList.size());
//            productDAO.saveAll(addProdList);
//        }
//        catch(Exception ex){
//            ex.printStackTrace();
//        }
//    }
//
//    public ResponseEntity<ProdAPIResponse> addProduct(Product product){
//        ProdAPIResponse response = new ProdAPIResponse();
//        response.setStatus(0);
//        response.setOperation(OP_ADD);
//        try {
//            Product resultProduct =productDAO.save(product);
//            response.setProductList(Arrays.asList(resultProduct));
//            return new ResponseEntity<ProdAPIResponse>(response, HttpStatus.OK);
//        }
//        catch(TransactionSystemException ex){
//            ex.printStackTrace();
//            response.setStatus(-1);
//            if(ex.getRootCause() instanceof ConstraintViolationException){
//                Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) ex.getRootCause()).getConstraintViolations();
//                StringBuilder error = new StringBuilder("|");
//                for(ConstraintViolation violation : constraintViolations){
//                    error.append(violation.getMessageTemplate());
//                    error.append("|");
//                }
//                response.setError(new ErrorDetails(200, error.toString()));
//            }
//            return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.BAD_REQUEST);
//        }
//        catch (Exception ex){
//            ex.printStackTrace();
//            response.setStatus(-1);
//            response.setError(new ErrorDetails(500, ex.getMessage()));
//            return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//    public ResponseEntity<ProdAPIResponse> addProductDetails(List<Product> productList){
//         ProdAPIResponse response = new ProdAPIResponse();
//         response.setStatus(0);
//         response.setOperation(OP_ADD);
//         try {
//             List<Product> resultList =productDAO.saveAll(productList);
//             response.setProductList(resultList);
//             return new ResponseEntity<ProdAPIResponse>(response, HttpStatus.OK);
//         }
//         catch(TransactionSystemException ex){
//             ex.printStackTrace();
//             response.setStatus(-1);
//             if(ex.getRootCause() instanceof ConstraintViolationException){
//                 Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) ex.getRootCause()).getConstraintViolations();
//                 StringBuilder error = new StringBuilder("|");
//                 for(ConstraintViolation violation : constraintViolations){
//                     error.append(violation.getMessageTemplate());
//                     error.append("|");
//                 }
//                 response.setError(new ErrorDetails(200, error.toString()));
//             }
//             return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.BAD_REQUEST);
//         }
//         catch (Exception ex){
//             ex.printStackTrace();
//             response.setStatus(-1);
//             response.setError(new ErrorDetails(500, ex.getMessage()));
//             return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
//         }
//    }
//
//    public ResponseEntity<ProdAPIResponse> getProductDetailsById(int id){
//        ProdAPIResponse response = new ProdAPIResponse();
//        response.setOperation(OP_GET);
//        try {
//            Product product = productDAO.getById(id);
//            List<Product> productList = new ArrayList<>();
//            if (product.getProductName() != null) {
//                productList.add(product);
//            }
//            response.setProductList(productList);
//            response.setStatus(0);
//            return new ResponseEntity<ProdAPIResponse>(response, HttpStatus.OK);
//        }
//        catch (EntityNotFoundException ex){
//            response.setStatus(-1);
//            response.setError(new ErrorDetails(100,"Record Not Found. Please Check Product ID"));
//            return new ResponseEntity<ProdAPIResponse>(response, HttpStatus.NOT_FOUND);
//        }
//        catch (Exception ex){
//            response.setStatus(-1);
//            response.setError(new ErrorDetails(500, ex.getMessage()));
//            return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    public ResponseEntity<ProdAPIResponse> getProductDetailsByIdList(List<Integer> idList) {
//        ProdAPIResponse response = new ProdAPIResponse();
//        try {
//            List<Product> productList = productDAO.getByIdCollection(idList);
//            response.setOperation(OP_GET);
//            response.setStatus(0);
//            response.setProductList(productList);
//
//      //  List<Integer> idNF = productList
////                .stream()
////                .filter(prod -> !idList.contains(prod.getProductId())).map(prod -> prod.getProductId()).collect(Collectors.toList());
//
//           List<Integer> idNF = idList.stream().filter(id -> productList.stream().noneMatch(prod -> prod.getProductId() == id)).collect(Collectors.toList());
//
//            if (idNF.size() > 0) {
//                response.setStatus(-1);
//                response.setProductList(productList);
//                response.setError(new ErrorDetails(100, "Record Not Found for " + idNF));
//                return new ResponseEntity<ProdAPIResponse>(response, HttpStatus.NOT_FOUND);
//            }
//        }
//        catch (Exception ex){
//            ex.printStackTrace();
//            response.setStatus(-1);
//            response.setError(new ErrorDetails(500, ex.getMessage()));
//            return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.OK);
//    }
//
//    public ResponseEntity<ProdAPIResponse> getProductDetailsByName(String productName) {
//
//        ProdAPIResponse response = new ProdAPIResponse();
//        logger.info(productName);
//        try {
//            List<Product> productList = productDAO.getByName("%" + productName + "%");
//            response.setProductList(productList);
//            response.setOperation(OP_GET);
//            response.setStatus(0);
////        List<Product> idNF = productList.stream().filter(name -> productList.stream().
////                noneMatch(s->name.equals(s.getProductName()))).collect(Collectors.toList());
//
//            if (productList.size() < 1) {
//                response.setStatus(-1);
//                response.setProductList(productList);
//                response.setError(new ErrorDetails(100, "No Records Found "));
//                return new ResponseEntity<ProdAPIResponse>(response, HttpStatus.NOT_FOUND);
//            }
//        }
//        catch (Exception ex){
//            response.setStatus(-1);
//            response.setError(new ErrorDetails(500,ex.getMessage()));
//            return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.OK);
//        }
//
//    public ResponseEntity<ProdAPIResponse> updateProductDetails(Product product) {
//        ProdAPIResponse response = new ProdAPIResponse();
//        response.setStatus(0);
//        response.setOperation(OP_UPD);
//        try {
//            if (productDAO.existsById(product.getProductId())) {
//                Product prod = productDAO.save(product);
//            }
//            else{
//                response.setStatus(-1);
//                response.setError(new ErrorDetails(100, "ID Not Found"));
//                return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.NOT_FOUND);
//            }
//        }
//        catch (Exception ex){
//            response.setStatus(-1);
//            response.setError(new ErrorDetails(100, ex.getMessage()));
//            return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.OK);
//    }
//
//    public ResponseEntity<ProdAPIResponse> deleteproductDetails(List<Integer> idList) {
//        ProdAPIResponse response = new ProdAPIResponse();
//        response.setStatus(0);
//        response.setOperation(OP_REM);
//        try
//        {
//            productDAO.deleteAllById(idList);
//        }
//        catch (EmptyResultDataAccessException ex){
//            response.setError(new ErrorDetails(100, "ID Cannot be found"));
//            response.setStatus(-1);
//            return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.NOT_FOUND);
//        }
//        catch (Exception ex){
//            response.setStatus(-1);
//            response.setError(new ErrorDetails(500, ex.getMessage()));
//            return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.OK);
//    }
//
//
//    public ResponseEntity<ProdAPIResponse> deleteAllproducts() {
//        ProdAPIResponse response = new ProdAPIResponse();
//        response.setOperation(OP_REM);
//        response.setStatus(0);
//        try {
//            productDAO.deleteAll();
//        }
//        catch (Exception ex){
//            response.setStatus(-1);
//            response.setError(new ErrorDetails(500, ex.getMessage()));
//            return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.OK);
//    }
//
//    public ResponseEntity<ProdAPIResponse> getAllProducts() {
//        ProdAPIResponse response = new ProdAPIResponse();
//        response.setOperation(OP_GET);
//        response.setStatus(0);
//        try {
//            List<Product> prodList = productDAO.findAll();
//            response.setProductList(prodList);
//        }
//        catch (Exception ex){
//            response.setStatus(-1);
//            response.setError(new ErrorDetails(500, ex.getMessage()));
//            return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<ProdAPIResponse>(response,HttpStatus.OK);
//    }
//}
