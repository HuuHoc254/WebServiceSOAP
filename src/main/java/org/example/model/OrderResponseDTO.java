package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private LocalDateTime orderDate;
    private String productCode;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
    private String customerName;
    private String phoneNumber;
    private String address;
    private String orderStatusName;
    private LocalDateTime allocationDate;
    private String accountName;
    private String staffName;
    private int version;
    private boolean isDeleted;

}