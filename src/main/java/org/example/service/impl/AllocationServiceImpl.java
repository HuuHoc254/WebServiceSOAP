package org.example.service.impl;

import org.example.repository.AllocationRepository;
import org.example.service.AllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AllocationServiceImpl implements AllocationService {
    @Autowired
    private AllocationRepository allocationRepository;
    @Override
    public void allocation(Integer productId, Integer quantity) {
        allocationRepository.AllocateInventory(productId,quantity);
    }
}
