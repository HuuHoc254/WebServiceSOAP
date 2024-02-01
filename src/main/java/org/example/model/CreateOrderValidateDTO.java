package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;
import org.example.entity.ProductEntity;
import org.springframework.validation.Errors;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderValidateDTO {
    private CustomerEntity customer;
    private ProductEntity product;
    private Errors errors;
}