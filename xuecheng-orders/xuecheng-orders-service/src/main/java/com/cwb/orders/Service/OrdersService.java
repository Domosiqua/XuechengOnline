package com.cwb.orders.Service;

import com.cwb.orders.model.dto.AddOrderDto;
import com.cwb.orders.model.dto.PayRecordDto;

/**
 * @author CWB
 * @version 1.0
 * @Date 2023/10/27 21:10
 */
public interface OrdersService {

    PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);



}
