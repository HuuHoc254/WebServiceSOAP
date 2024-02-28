package org.example.dto.request.account;

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
            , "fullName"
            , "phoneNumber"
            , "pageNumber"
            , "pageSize"
            })
@XmlRootElement(name = "searchAccountRequest")
public class SearchAccountRequest {
    private String accountName;
    private String fullName;
    private String phoneNumber;
    @XmlElement(name = "pageNumber")
    private Integer pageNumber;
    @XmlElement(name = "pageSize")
    private Integer pageSize;
}
