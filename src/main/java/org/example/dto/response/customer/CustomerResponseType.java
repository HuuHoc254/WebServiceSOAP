package org.example.dto.response.customer;

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
public class CustomerResponseType {
    private Integer customerId;
    private String customerName;
    private String phoneNumber;
    private String address;
    private Integer accountId;
    private Integer version;
    private Boolean isDeleted;

}