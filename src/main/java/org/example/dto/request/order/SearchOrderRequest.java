package org.example.dto.request.order;

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
          "accountName"
        , "staffFullName"
          ,"productCode"
        , "productName"
        , "customerName"
        , "customerPhoneNumber"
        , "beginOrderDate"
        , "endOrderDate"
        , "order"
        , "allocation"
        , "pageIndex"
        , "pageSize"
})
@XmlRootElement(name = "searchOrderRequest")
public class SearchOrderRequest {
    private String accountName;
    private String staffFullName;
    private String productCode;
    private String productName;
    private String customerName;
    private String customerPhoneNumber;
    private String beginOrderDate;
    private String endOrderDate;
    private Integer order;
    private Integer allocation;
    @XmlElement(name = "pageIndex")
    private Integer pageIndex;
    @XmlElement(name = "pageSize")
    private Integer pageSize;
}
