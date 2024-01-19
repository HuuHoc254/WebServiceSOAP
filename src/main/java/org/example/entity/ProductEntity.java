package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "product")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;
    private String productCode;
    private String productName;
    private Double purchasePrice;
    private Double salePrice;
    private Integer inventory_quantity;
    private Integer version;
    private Boolean isDeleted;
    @OneToMany(mappedBy = "product",fetch = FetchType.EAGER)
    private List<OrderEntity> orderList;
}
