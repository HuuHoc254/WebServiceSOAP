package org.example.dto.response.order;

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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "listSaveOrderResponse", namespace = "http://yournamespace.com")
@XmlType(propOrder = { "status", "saveOrderResponses"})
public class ListSaveOrderResponse {
    private String status;
    @XmlElement(name = "errorTypes")
    private List<SaveOrderResponse> saveOrderResponses;

}
