package org.example.dto.request.customer;

import jakarta.xml.bind.annotation.*;
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
        , "pageNumber"
        , "pageSize"
})
@XmlRootElement(name = "searchCustomerRequest")
public class SearchCustomerRequest {
    private String customerName;
    private String phoneNumber;
    @XmlElement(name = "pageNumber")
    private Integer pageNumber;
    @XmlElement(name = "pageSize")
    private Integer pageSize;
}
