package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "orders")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
    private Double unitPrice;
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
    private String addressCustomer;
    private String phoneNumberCustomer;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
    private LocalDateTime orderDate;
    private LocalDateTime allocationDate;
    @ManyToOne
    @JoinColumn(name = "order_status_id")
    private OrderStatusEntity orderStatus;
    private Integer version;
    private Boolean isDeleted;
}
