package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.CustomerEntity;
import org.example.entity.ProductEntity;
import org.springframework.validation.Errors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAllocationValidateDTO {
    private ProductEntity product;
    private Errors errors;
}
