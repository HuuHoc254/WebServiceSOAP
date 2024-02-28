package org.example.dto.request.order;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "saveListOrderRequest", namespace = "http://yournamespace.com")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "listOrder" })
public class SaveListOrderRequest {
    @XmlElement(name = "order")
    private List<SaveOrderRequest> listOrder;
}
