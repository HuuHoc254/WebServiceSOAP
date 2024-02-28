package org.example.dto.response.account;

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
@XmlRootElement(name = "accountResponse", namespace = "http://yournamespace.com")
@XmlType(propOrder = { "status", "errorTypes" , "accountResponseType" })
public class AccountResponse {
    private String status;
    @XmlElement(name = "errorTypes")
    private List<ErrorTypeResponse> errorTypes;
    private AccountResponseType accountResponseType;
}
