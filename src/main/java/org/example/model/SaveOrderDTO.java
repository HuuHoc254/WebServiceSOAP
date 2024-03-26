package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaveOrderDTO {
    private Integer ordinalNumber;
    private Integer orderId;
    private Integer  productId;
    private Double unitPrice;
    private Integer quantity;
    private Integer customerId;
    private String phoneNumber;
    private String address;
    private Integer version;
    private Integer accountId;
}