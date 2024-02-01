package org.example.dto.response.order;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.response.ErrorTypeResponse;
import org.example.dto.response.account.AccountResponseType;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "createOrderResponse", namespace = "http://yournamespace.com")
@XmlType(propOrder = { "errorTypes", "status" , "orderResponseType" })
public class CreateOrderResponse {
    @XmlElement(name = "errorTypes")
    private List<ErrorTypeResponse> errorTypes;
    private String status;
    private OrderResponseType orderResponseType;
}
