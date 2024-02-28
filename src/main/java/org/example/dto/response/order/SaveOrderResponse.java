package org.example.dto.response.order;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
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
@XmlRootElement(name = "saveOrderResponse", namespace = "http://yournamespace.com")
@XmlType(propOrder = { "ordinalNumber", "errors" })
public class SaveOrderResponse {
    private String ordinalNumber;
    private List<String> errors;
}
