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
@XmlType(namespace = "http://yournamespace.com", propOrder ={
        "ordinalNumber"
        , "orderId"
        , "productCode"
        , "quantity"
        , "phoneNumberCustomer"
        , "version" })
public class SaveOrderRequest {
    private Integer ordinalNumber;
    private Integer orderId;
    private String  productCode;
    private Integer quantity;
    private String  phoneNumberCustomer;
    private Integer version;
}