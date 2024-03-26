package org.example.service;

import org.example.dto.request.order.SearchOrderRequest;
import org.example.entity.AccountEntity;
import org.example.model.SaveOrderDTO;

import java.util.List;
import java.util.Map;

public interface OrderService {

    int totalRecordSearch(SearchOrderRequest request, AccountEntity account);

    List<Map<String, Object>> search(SearchOrderRequest request, AccountEntity account, int recordNumber, int pageSize);


    int deleteOrder(Integer orderId);

    int totalFindCustomerZeroOrder( String  startDate
                                  , String  endDate);

    void saveOrder(StringBuilder sql);

    void saveAll(List<SaveOrderDTO> listOrder);

    List<Map<String, Object>> findCustomerZeroOrder(String startDate, String endDate, int recordNumber, int pageSize);

    int totalFindProductBestSeller(String startDate, String endDate);

    List<Map<String, Object>> findProductBestSeller(String startDate, String endDate, int recordNumber, int pageSize);

    int totalFindProductZeroOrder( String  startDate, String  endDate);

    List<Map<String, Object>> findProductZeroOrder(String startDate, String endDate, int recordNumber, int pageSize);

}
