package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "account")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;
    private String accountName;
    private String fullName;
    private String password;
    private String phoneNumber;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;
    private Boolean isOnline;
    private Integer version;
    private Boolean isDeleted;
    @OneToMany(mappedBy = "account",fetch = FetchType.EAGER)
    private List<CustomerEntity> customerList;
    @OneToMany(mappedBy = "account",fetch = FetchType.EAGER)
    private List<OrderEntity> orderList;
    @OneToMany(mappedBy = "account",fetch = FetchType.EAGER)
    private List<AllocationEntity> allocationList;
}
