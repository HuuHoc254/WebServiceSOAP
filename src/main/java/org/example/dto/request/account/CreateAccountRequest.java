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
          "accountName"
        , "fullName"
        , "password"
        , "confirmPassword"
        , "phoneNumber"  })
@XmlRootElement(name = "createAccountRequest")
public class CreateAccountRequest {
    // getters, setters, constructors
    //    @XmlElement
    private String accountName;
    private String fullName;
    private String password;
    private String confirmPassword;
    private String phoneNumber;

}