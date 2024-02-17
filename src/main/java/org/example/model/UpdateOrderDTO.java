package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderDTO {
    private Integer orderId;
    private Integer productId;
    private Integer customerId;
    private Integer quantity;
    private Integer version;
}
