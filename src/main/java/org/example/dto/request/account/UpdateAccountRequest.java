package org.example.dto.request.account;

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
          "accountId"
        , "accountName"
        , "password"
        , "confirmPassword"
        , "fullName"
        , "phoneNumber"
        ,  "version"  })
@XmlRootElement(name = "updateAccountRequest")
public class UpdateAccountRequest {
    private Integer accountId;
    private String accountName;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String phoneNumber;
    private Integer version;
}