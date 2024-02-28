package org.example.dto.response.report;

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
@XmlRootElement(name = "products", namespace = "http://yournamespace.com")
@XmlType
public class ProductsReportResponse {
    @XmlElement(name = "product")
    private List<ProductReportResponse> productsBestSeller;
}