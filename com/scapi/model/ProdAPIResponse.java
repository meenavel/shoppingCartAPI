package com.scapi.model;

import com.scapi.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdAPIResponse {
    private int status;
    private String operation;
    private List<Product> productList;
    private ErrorDetails error;
}
