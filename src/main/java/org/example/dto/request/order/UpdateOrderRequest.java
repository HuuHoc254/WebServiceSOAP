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
@XmlType(propOrder ={
          "orderId"
        , "productCode"
        , "productName"
        , "quantity"
        , "customerName"
        , "phoneNumber"
        , "version" })
@XmlRootElement(name = "updateOrderRequest")
public class UpdateOrderRequest {
    private Integer orderId;
    private String productCode;
    private String productName;
    private Integer quantity;
    private String customerName;
    private String phoneNumber;
    private Integer version;
}