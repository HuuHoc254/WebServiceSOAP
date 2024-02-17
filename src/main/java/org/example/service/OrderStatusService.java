package org.example.service;

import org.example.entity.OrderStatusEntity;

public interface OrderStatusService {
    OrderStatusEntity findById(int id);
}
