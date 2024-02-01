package org.example.dto.response.order;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.response.ErrorTypeResponse;
import org.example.dto.response.product.ProductResponseType;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "orderResponseList", namespace = "http://yournamespace.com")
@XmlType
public class OrderResponseList {
    @XmlElement(name = "errorTypes")
    private List<ErrorTypeResponse> errorTypes;
    private String message;
    @XmlElement(name = "orderResponse")
    private List<OrderResponseType> orderResponses;
}