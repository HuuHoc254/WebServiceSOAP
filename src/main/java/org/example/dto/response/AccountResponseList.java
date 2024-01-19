package org.example.dto.response;

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
@XmlRootElement(name = "accountResponseList", namespace = "http://yournamespace.com")
@XmlType
public class AccountResponseList {
    @XmlElement(name = "accountResponse")
    private List<AccountResponseType> accountResponses;

  }