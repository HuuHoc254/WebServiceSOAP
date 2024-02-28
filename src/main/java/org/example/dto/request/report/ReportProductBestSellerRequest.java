package org.example.dto.request.report;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "reportProductBestSellerRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportProductBestSellerRequest {
    private String startDate;
    private String endDate;
    @XmlElement(name = "pageNumber")
    private Integer pageNumber;
    @XmlElement(name = "pageSize")
    private Integer pageSize;
}
