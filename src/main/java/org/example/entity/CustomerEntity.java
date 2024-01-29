package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "customer")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;
    private String customerName;
    private String phoneNumber;
    @ManyToOne
    @JoinColumn(name = "administrator_id")
    private AccountEntity account;
    private String address;
    private Integer version;
    private Boolean isDeleted;
    @OneToMany(mappedBy = "customer",fetch = FetchType.EAGER)
    private List<OrderEntity> orderList;
}
