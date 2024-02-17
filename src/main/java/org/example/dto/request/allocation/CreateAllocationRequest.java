package org.example.dto.request.allocation;

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
            , "quantity"})
@XmlRootElement(name = "createAllocationRequest")
public class CreateAllocationRequest {
    private String productCode;
    private String productName;
    private Integer quantity;
}