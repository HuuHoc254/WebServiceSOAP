package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


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
    private LocalDate orderDate;
    private LocalDate allocationDate;
    @ManyToOne
    @JoinColumn(name = "order_status_id")
    private OrderStatusEntity orderStatus;
    private Integer version;
    private Boolean idDeleted;
}
