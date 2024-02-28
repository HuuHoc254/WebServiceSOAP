package org.example.dto.response.product;

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
@XmlRootElement(name = "productResponseList", namespace = "http://yournamespace.com")
@XmlType
public class ProductResponseList {
    @XmlElement(name = "productResponse")
    private List<ProductResponseType> productResponses;
}