package org.example.dto.request.customer;

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
        "customerName"
        , "phoneNumber"
        , "accountId"
        , "address"  })
@XmlRootElement(name = "createCustomerRequest")
public class CreateCustomerRequest {
    private String customerName;
    private String phoneNumber;
    private Integer accountId;
    private String address;
}
