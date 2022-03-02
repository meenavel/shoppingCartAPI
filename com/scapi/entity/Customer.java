package com.scapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.util.List;

import static javax.persistence.GenerationType.AUTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = AUTO)
    private int custId;
    @NotBlank(message = "Customer Name is Mandatory")
    private String custName;

    @NotBlank(message = "Customer Address is Mandatory")
    private String address;

    @NotBlank(message = "Customer Pincode is Mandatory")
    private String pinCode;

    @NotBlank(message = "Phone Number is Mandatory")
    private String phoneNo;

    @NotBlank(message = "EmailId is Mandatory")
    private String mailId;

    @JsonManagedReference
    @OneToMany(mappedBy = "customer")
    List<Cart> cartList;

}
