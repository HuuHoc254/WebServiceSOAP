package org.example.dto.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
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
@XmlType
public class AccountResponseType {
    private Integer accountId;
    private String accountName;
    private String fullName;
    private String phoneNumber;
    private Boolean isOnline;
    private Integer version;
    private Boolean isDeleted;

    // Getter và Setter cho các trường

    // ... (Các getter và setter khác)
}