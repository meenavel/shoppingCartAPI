package com.scapi.model;

import com.scapi.entity.Cart;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartAPIResponse {
    private int status;
    private String operation;
    private Cart cart;
    private ErrorDetails error;
}
