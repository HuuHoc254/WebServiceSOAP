package org.example.dto.response.product;

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
@XmlRootElement(name = "customerResponse", namespace = "http://yournamespace.com")
@XmlType
public class ProductResponseType {
    private Integer productId;
    private String productCode;
    private String productName;
    private Double purchasePrice;
    private Double salePrice;
    private Integer inventory_quantity;
    private Integer version;
    private Boolean isDeleted;
}