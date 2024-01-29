package org.example.dto.request.product;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "loadAllProduct")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetProductsRequest {
    @XmlElement(name = "pageIndex")
    private Integer pageIndex;
    @XmlElement(name = "pageSize")
    private Integer pageSize;
}
