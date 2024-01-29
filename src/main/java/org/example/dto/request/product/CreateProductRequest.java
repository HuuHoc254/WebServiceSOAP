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

@XmlType(propOrder = {
          "productCode"
        , "productName"
        , "purchasePrice"
        , "salePrice" })
@XmlRootElement(name = "createProductRequest")
public class CreateProductRequest {
    private String productCode;
    private String productName;
    private Double purchasePrice;
    private Double salePrice;

}