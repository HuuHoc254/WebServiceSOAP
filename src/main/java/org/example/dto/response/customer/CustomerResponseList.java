package org.example.dto.response.customer;

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
@XmlRootElement(name = "customerResponseList", namespace = "http://yournamespace.com")
@XmlType
public class CustomerResponseList {
    @XmlElement(name = "customerResponse")
    private List<CustomerResponseType> customerResponses;
}