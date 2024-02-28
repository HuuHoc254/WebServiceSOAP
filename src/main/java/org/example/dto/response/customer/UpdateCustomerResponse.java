package org.example.dto.response.customer;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.response.ErrorTypeResponse;

import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "updateCustomerResponse", namespace = "http://yournamespace.com")
@XmlType(propOrder = { "errorTypes", "message" , "customerResponseType" })
public class UpdateCustomerResponse {
    @XmlElement(name = "errorTypes")
    private List<ErrorTypeResponse> errorTypes;
    private String message;
    private CustomerResponseType customerResponseType;
}
