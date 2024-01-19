package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_status")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderStatusId;
    private String orderStatusName;
}
