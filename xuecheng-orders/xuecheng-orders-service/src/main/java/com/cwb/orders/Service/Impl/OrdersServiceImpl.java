package com.cwb.orders.Service.Impl;

import com.cwb.orders.Service.OrdersService;
import com.cwb.orders.mapper.XcOrdersGoodsMapper;
import com.cwb.orders.mapper.XcOrdersMapper;
import com.cwb.orders.mapper.XcPayRecordMapper;
import com.cwb.orders.model.dto.AddOrderDto;
import com.cwb.orders.model.dto.PayRecordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @author CWB
 * @version 1.0
 * @Date 2023/10/27 21:10
 */
@Service
public class OrdersServiceImpl implements OrdersService {


    @Autowired
    XcOrdersMapper xcOrdersMapper;
    @Autowired
    XcOrdersGoodsMapper xcOrdersGoodsMapper;
    @Autowired
    XcPayRecordMapper xcPayRecordMapper;

    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {





        return null;

    }
}
