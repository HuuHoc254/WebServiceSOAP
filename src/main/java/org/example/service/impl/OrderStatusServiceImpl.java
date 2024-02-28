package org.example.service.impl;

import org.example.entity.OrderStatusEntity;
import org.example.repository.OrderStatusRepository;
import org.example.service.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusServiceImpl implements OrderStatusService {
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Override
    public OrderStatusEntity findById(int id) {
        return orderStatusRepository.findById(id).orElse(null);
    }
}
