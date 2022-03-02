package com.scapi.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int productId;

    @NotBlank(message = "Product Name is Mandatory")
    @Column(length = 50, nullable = false)
    private String productName;

    @NotBlank(message = "Product Description is Mandatory")
    private String productDescription;

    @NotNull(message = "Product Amount is Mandatory")
    private Double productAmount;

    private int productSoldCount;
}
