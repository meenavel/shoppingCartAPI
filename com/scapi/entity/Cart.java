package com.scapi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cartId;

    private LocalDate orderDate;

    @NotBlank(message = "Product List is Mandatory")
    private String productList;

    private double cartAmount;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "cust_Id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

}
