package com.cwb.orders.Service;

import com.cwb.orders.model.dto.AddOrderDto;
import com.cwb.orders.model.dto.PayRecordDto;
import com.cwb.orders.model.dto.PayStatusDto;
import com.cwb.orders.model.po.XcPayRecord;

/**
 * @author CWB
 * @version 1.0
 * @Date 2023/10/27 21:10
 */
public interface OrdersService {

    PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    XcPayRecord getPayRecordByPayno(String payNo);

    PayRecordDto queryPayResult(String payNo);

    PayStatusDto queryPayResultFromAlipay(String payNo);

    void saveAliPayStatus(PayStatusDto payStatusDto);
}
