package org.example.dto.response.product;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.response.ErrorTypeResponse;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "updateProductResponse", namespace = "http://yournamespace.com")
@XmlType(propOrder = { "errorTypes", "status" , "productResponseType" })
public class UpdateProductResponse {
    @XmlElement(name = "errorTypes")
    private List<ErrorTypeResponse> errorTypes;
    private String status;
    private ProductResponseType productResponseType;
}
