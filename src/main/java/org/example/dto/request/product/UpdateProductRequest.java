package org.example.dto.request.product;

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
        "productId"
        , "productCode"
        , "productName"
        , "purchasePrice"
        , "salePrice"
        , "version" })
@XmlRootElement(name = "updateProductRequest")
public class UpdateProductRequest {
    private Integer productId;
    private String productCode;
    private String productName;
    private Double purchasePrice;
    private Double salePrice;
    private Integer version;
}