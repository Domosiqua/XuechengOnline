package com.cwb.orders.api;

import com.cwb.base.utils.QRCodeUtil;
import com.cwb.orders.Service.OrdersService;
import com.cwb.orders.model.dto.AddOrderDto;
import com.cwb.orders.model.dto.PayRecordDto;
import com.cwb.orders.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Security;

/**
 * @author CWB
 * @version 1.0
 * @Date 2023/10/27 21:00
 */
@Api(value = "订单支付接口", tags = "订单支付接口")
@Slf4j
@Controller
public class OrdersController {

    @Autowired
    OrdersService ordersService;

    @ApiOperation("生成支付二维码")
    @PostMapping("/generatepaycode")
    @ResponseBody
    public PayRecordDto generatePayCode(@RequestBody AddOrderDto addOrderDto) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String id = user.getId();
        PayRecordDto order = ordersService.createOrder(id, addOrderDto);

        return order;
    }

    @ApiOperation("扫码下单接口")
    @GetMapping("/requestpay")
    public void requestpay(String payNo, HttpServletResponse httpResponse) throws IOException {

    }

}