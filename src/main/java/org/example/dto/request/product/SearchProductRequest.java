package org.example.dto.request.product;

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
        "productCode"
        , "productName"
        , "pageIndex"
        , "pageSize"
})
@XmlRootElement(name = "searchProductRequest")
public class SearchProductRequest {
    private String productCode;
    private String productName;
    @XmlElement(name = "pageIndex")
    private Integer pageIndex;
    @XmlElement(name = "pageSize")
    private Integer pageSize;
}

