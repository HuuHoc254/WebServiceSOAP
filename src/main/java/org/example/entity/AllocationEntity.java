package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "allocation")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AllocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer allocationId;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
    private String address;
    private Integer quantity;
    private LocalDate allocationDate;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
}
