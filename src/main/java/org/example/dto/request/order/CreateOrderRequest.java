package org.example.dto.request.order;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "productCode"
        , "productName"
        , "unitPrice"
        , "quantity"
        , "customerName"
        , "phoneNumberCustomer"
        , "addressCustomer"
        , "accountName"
        , "fullName" })
@XmlRootElement(name = "createOrderRequest")
public class CreateOrderRequest {
    private String productCode;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
    private String customerName;
    private String phoneNumberCustomer;
    private String addressCustomer;
    private String accountName;
    private String fullName;
}