package org.example.service.impl;

import jakarta.persistence.EntityManager;
import org.example.dto.request.order.SearchOrderRequest;
import org.example.entity.AccountEntity;
import org.example.model.SaveOrderDTO;
import org.example.repository.OrderRepository;
import org.example.service.AccountService;
import org.example.service.OrderService;
import org.example.service.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    EntityManager entityManager;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OrderStatusService orderStatusService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public int totalRecordSearch(SearchOrderRequest request, AccountEntity account) {

        LocalDate beginDate = LocalDate.parse(request.getBeginOrderDate(), formatter);
        LocalDate endDate = LocalDate.parse(request.getEndOrderDate(), formatter);

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
                            ,request.getOrder()
                            ,request.getAllocation()
                            ,account.getRole().getRoleId()==1);

    }

    @Override
    public List<Map<String, Object>> search(SearchOrderRequest request, AccountEntity account, int recordNumber, int pageSize) {
        LocalDate beginDate = LocalDate.parse(request.getBeginOrderDate(), formatter);
        LocalDate endDate = LocalDate.parse(request.getEndOrderDate(), formatter);
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
                            ,request.getOrder()
                            ,request.getAllocation()
                            ,recordNumber
                            ,pageSize
                            ,account.getRole().getRoleId()==1);
        }

    @Override
    public int deleteOrder(Integer orderId) {
        return orderRepository.deleteOrder(orderId);
    }

    @Override
    public int totalFindCustomerZeroOrder(String startDate, String endDate) {
        return orderRepository.totalFindCustomerZeroOrder(startDate,endDate);
    }

    @Override
    public void saveOrder(StringBuilder sql) {
        String[] split = sql.toString().split(";");
        for (String s : split) {
//          Kiểm tra xem câu lệnh có rỗng không trước khi thực hiện truy vấn
            if (!s.trim().isEmpty()) {
                int rowUpdate = entityManager.createNativeQuery(s).executeUpdate();
                if(rowUpdate==0) throw new RuntimeException("UPDATE khong thanh cong!");
            }
        }
    }

    @Override
    public void saveAll(List<SaveOrderDTO> listOrder) {
        listOrder.forEach(o ->{
            if(o.getOrderId()!=0){
                int rowUpdate = orderRepository.updateOrder(
                                                         o.getOrderId()
                                                        ,o.getProductId()
                                                        ,o.getUnitPrice()
                                                        ,o.getQuantity()
                                                        ,o.getCustomerId()
                                                        ,o.getAddress()
                                                        ,o.getPhoneNumber()
                                                        ,o.getVersion()
                                                        );
                if(rowUpdate<1){
                    throw new RuntimeException("Phiên bản của đơn hàng "+ o.getOrdinalNumber() + " không trùng khớp!");
                }
            }else {
                int rowUpdate = orderRepository.insertOrder(
                                                         o.getProductId()
                                                        ,o.getUnitPrice()
                                                        ,o.getQuantity()
                                                        ,o.getCustomerId()
                                                        ,o.getPhoneNumber()
                                                        ,o.getAddress()
                                                        ,o.getAccountId()
                                                            );
                if(rowUpdate<1){
                    throw new RuntimeException("Đơn hàng "+ o.getOrdinalNumber() + " không thêm được!");
                }
            }
        });
    }

    @Override
    public List<Map<String, Object>> findCustomerZeroOrder(String startDate, String endDate, int recordNumber, int pageSize) {
        return orderRepository.findCustomerZeroOrder(startDate,endDate,recordNumber,pageSize);
    }

    @Override
    public int totalFindProductBestSeller(String startDate, String endDate) {
        return orderRepository.totalFindProductBestSeller(startDate,endDate);
    }

    @Override
    public List<Map<String, Object>> findProductBestSeller(String startDate, String endDate, int recordNumber, int pageSize) {
        return orderRepository.findProductsBestSeller(startDate,endDate,recordNumber,pageSize);
    }

    @Override
    public int totalFindProductZeroOrder(String startDate, String endDate) {
        return orderRepository.totalFindProductZeroOrder(startDate,endDate);
    }

    @Override
    public List<Map<String, Object>> findProductZeroOrder(String startDate, String endDate, int recordNumber, int pageSize) {
        return orderRepository.findProductZeroOrders(startDate,endDate,recordNumber,pageSize);
    }

}
