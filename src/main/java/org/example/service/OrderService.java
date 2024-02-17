package org.example.service;

import org.example.dto.request.order.SearchOrderRequest;
import org.example.entity.AccountEntity;
import org.example.entity.CustomerEntity;
import org.example.entity.OrderEntity;
import org.example.entity.ProductEntity;
import org.example.model.UpdateOrderDTO;

import java.util.List;

public interface OrderService {
    OrderEntity createOrder(CustomerEntity customer, ProductEntity product, Integer quantity);

    int totalRecordFindAll(AccountEntity account);

    List<OrderEntity> findAll(AccountEntity account, int recordNumber, int pageSize);

    int totalRecordSearch(SearchOrderRequest request, AccountEntity account);

    List<OrderEntity> search(SearchOrderRequest request, AccountEntity account, int recordNumber, int pageSize);

    int updateOrder(UpdateOrderDTO updateOrderDTO);

    OrderEntity findById(Integer orderId);

    int deleteOrder(Integer orderId);
}
