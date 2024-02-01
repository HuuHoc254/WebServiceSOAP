package org.example.dto.response.order;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class OrderResponseType {
    private Integer orderId;
    private String orderDateTime;
    private String productCode;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
    private String customerName;
    private String customerPhoneNumber;
    private String address;
    private String orderStatusName;
    private String allocationDateTime;
    private String accountName;
    private String staffFullName;
    private Integer version;
    private Boolean isDeleted;

    // Getter và Setter cho các trường

    // ... (Các getter và setter khác)
}