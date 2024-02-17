package org.example.service.impl;

import org.example.dto.request.order.SearchOrderRequest;
import org.example.entity.*;
import org.example.model.UpdateOrderDTO;
import org.example.repository.OrderRepository;
import org.example.security.UserDetailImpl;
import org.example.service.AccountService;
import org.example.service.OrderService;
import org.example.service.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OrderStatusService orderStatusService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public OrderEntity createOrder(CustomerEntity customer, ProductEntity product, Integer quantity) {
        UserDetailImpl userDetails =(UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AccountEntity account = accountService.findByAccountName(userDetails.getAccountName());
        LocalDateTime dateTime = LocalDateTime.now();
        OrderEntity order = new OrderEntity();
        order.setAccount(account);
        order.setOrderDate(dateTime);
        order.setProduct(product);
        order.setCustomer(customer);
        order.setAddressCustomer(customer.getAddress());
        order.setPhoneNumberCustomer(customer.getPhoneNumber());
        order.setUnitPrice(product.getSalePrice());
        order.setQuantity(quantity);
        order.setVersion(0);
        order.setIsDeleted(false);
        OrderStatusEntity orderStatus = orderStatusService.findById(1);
        order.setOrderStatus(orderStatus);
        return orderRepository.save(order);
    }

    @Override
    public int totalRecordFindAll(AccountEntity account) {
        if( (account.getRole().getRoleId())==1 ){
            return orderRepository.totalRecordFindAll(true, account.getAccountId());
        }
        return orderRepository.totalRecordFindAll(false, account.getAccountId());
    }

    @Override
    public List<OrderEntity> findAll(AccountEntity account, int recordNumber, int pageSize) {
        if( (account.getRole().getRoleId())==1){
            return orderRepository.findAll(true,account,recordNumber, pageSize);
        }
        return orderRepository.findAll(false,account,recordNumber,pageSize);
    }

    @Override
    public int totalRecordSearch(SearchOrderRequest request, AccountEntity account) {

        LocalDate beginDate = LocalDate.parse(request.getBeginOrderDate(), formatter);
        LocalDate endDate = LocalDate.parse(request.getEndOrderDate(), formatter);

        if(account.getRole().getRoleId()==1){
            return orderRepository
                    .totalRecordSearch(
                            account.getAccountId()
                            ,request.getAccountName()
                            ,request.getStaffFullName()
                            ,request.getProductCode()
                            ,request.getProductName()
                            ,request.getCustomerName()
                            ,request.getCustomerPhoneNumber()
                            ,beginDate.atStartOfDay()
                            ,endDate.atTime(23,59)
                            ,request.getOrderStatusId()
                            ,true);
        }
        return orderRepository
                .totalRecordSearch(
                        account.getAccountId()
                        ,request.getAccountName()
                        ,request.getStaffFullName()
                        ,request.getProductCode()
                        ,request.getProductName()
                        ,request.getCustomerName()
                        ,request.getCustomerPhoneNumber()
                        ,beginDate.atStartOfDay()
                        ,endDate.atTime(23,59)
                        ,request.getOrderStatusId()
                        ,false);
    }

    @Override
    public List<OrderEntity> search(SearchOrderRequest request, AccountEntity account, int recordNumber, int pageSize) {
        LocalDate beginDate = LocalDate.parse(request.getBeginOrderDate(), formatter);
        LocalDate endDate = LocalDate.parse(request.getEndOrderDate(), formatter);

        if(account.getRole().getRoleId()==1){
            return orderRepository
                    .search(
                            account.getAccountId()
                            ,request.getAccountName()
                            ,request.getStaffFullName()
                            ,request.getProductCode()
                            ,request.getProductName()
                            ,request.getCustomerName()
                            ,request.getCustomerPhoneNumber()
                            ,beginDate.atStartOfDay()
                            ,endDate.atTime(23,59)
                            ,request.getOrderStatusId()
                            ,recordNumber
                            ,pageSize
                            ,true);
        }
         return orderRepository
                .search(
                        account.getAccountId()
                        ,request.getAccountName()
                        ,request.getStaffFullName()
                        ,request.getProductCode()
                        ,request.getProductName()
                        ,request.getCustomerName()
                        ,request.getCustomerPhoneNumber()
                        ,beginDate.atStartOfDay()
                        ,endDate.atTime(23,59)
                        ,request.getOrderStatusId()
                        ,recordNumber
                        ,pageSize
                        ,false);
    }

    @Override
    public int updateOrder(UpdateOrderDTO updateOrderDTO) {
        return orderRepository.updateOrder(
                          updateOrderDTO.getOrderId()
                        , updateOrderDTO.getProductId()
                        , updateOrderDTO.getCustomerId()
                        , updateOrderDTO.getQuantity()
                        , updateOrderDTO.getVersion());
    }

    @Override
    public OrderEntity findById(Integer orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Override
    public int deleteOrder(Integer orderId) {
        return orderRepository.deleteOrder(orderId);
    }

}
