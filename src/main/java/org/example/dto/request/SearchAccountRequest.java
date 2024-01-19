package org.example.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
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
            })
@XmlRootElement(name = "searchAccountRequest", namespace = "http://yournamespace.com")
public class SearchAccountRequest {
    private String accountName;
    private String fullName;
    private String phoneNumber;
}
