package com.cwb.orders.Service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cwb.base.exception.XcException;
import com.cwb.base.utils.IdWorkerUtils;
import com.cwb.base.utils.QRCodeUtil;
import com.cwb.orders.Service.OrdersService;
import com.cwb.orders.config.AlipayConfig;
import com.cwb.orders.mapper.XcOrdersGoodsMapper;
import com.cwb.orders.mapper.XcOrdersMapper;
import com.cwb.orders.mapper.XcPayRecordMapper;
import com.cwb.orders.model.dto.AddOrderDto;
import com.cwb.orders.model.dto.PayRecordDto;
import com.cwb.orders.model.dto.PayStatusDto;
import com.cwb.orders.model.po.XcOrders;
import com.cwb.orders.model.po.XcOrdersGoods;
import com.cwb.orders.model.po.XcPayRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author CWB
 * @version 1.0
 * @Date 2023/10/27 21:10
 */
@Service
@Slf4j
public class OrdersServiceImpl implements OrdersService {


    @Autowired
    XcOrdersMapper xcOrdersMapper;
    @Autowired
    XcOrdersGoodsMapper xcOrdersGoodsMapper;
    @Autowired
    XcPayRecordMapper xcPayRecordMapper;
    @Autowired
    OrdersService currentProxy;

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;


    @Value("${pay.qrcodeurl}")
    String qrcodeurl;


    @Override
    @Transactional
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {


        //添加商品订单
        XcOrders xcOrders = saveXcOrders(userId, addOrderDto);
        //添加支付交易记录
        XcPayRecord payRecode = createPayRecode(xcOrders);
        //生成二维码
        PayRecordDto payRecordDto=new PayRecordDto();
        BeanUtils.copyProperties(payRecode,payRecordDto);
        Long payNo = payRecode.getPayNo();
        String url = String.format(qrcodeurl, payNo);
        String qrCode=null;
        try {
            qrCode = new QRCodeUtil().createQRCode(url, 200, 200);
        } catch (IOException e) {
            XcException.cast("生成二维码失败");
        }
        payRecordDto.setQrcode(qrCode);

        return payRecordDto;

    }
    public XcPayRecord createPayRecode(XcOrders xcOrders){
        //不存在或者状态为已支付
        if (xcOrders==null)
            XcException.cast("订单不存在");
        if("600002".equals(xcOrders.getStatus()))
            XcException.cast("请勿重复支付");


        XcPayRecord xcPayRecord = new XcPayRecord();
        xcPayRecord.setPayNo(IdWorkerUtils.getInstance().nextId());
        xcPayRecord.setOrderId(xcOrders.getId());
        xcPayRecord.setOrderName(xcOrders.getOrderName());
        xcPayRecord.setTotalPrice(xcOrders.getTotalPrice());
        xcPayRecord.setCurrency("CNY");
        xcPayRecord.setStatus("601001");
        xcPayRecord.setUserId(xcOrders.getUserId());
        int insert = xcPayRecordMapper.insert(xcPayRecord);
        if (insert<1)
            XcException.cast("支付记录插入失败");


        return xcPayRecord;
    }

    public XcOrders saveXcOrders(String userID,AddOrderDto addOrderDto){
        XcOrders xcOrders = getOrderByBusinessId(addOrderDto.getOutBusinessId());
        if (xcOrders!=null)
            return xcOrders;

        IdWorkerUtils instance = IdWorkerUtils.getInstance();
        long orderID = instance.nextId();

        xcOrders=new XcOrders();
        xcOrders.setId(orderID);
        xcOrders.setTotalPrice(addOrderDto.getTotalPrice());
        xcOrders.setStatus("600001");
        xcOrders.setUserId(userID);
        xcOrders.setOrderType(addOrderDto.getOrderType());
        xcOrders.setOrderName(addOrderDto.getOrderName());
        xcOrders.setOrderDescrip(addOrderDto.getOrderDescrip());
        xcOrders.setOrderDetail(addOrderDto.getOrderDetail());
        xcOrders.setOutBusinessId(addOrderDto.getOutBusinessId());
        int insert = xcOrdersMapper.insert(xcOrders);
        if(insert<1)
            XcException.cast("添加订单失败");

        XcOrdersGoods xcOrdersGoods=new XcOrdersGoods();
        xcOrdersGoods.setOrderId(orderID);
        String orderDetail = addOrderDto.getOrderDetail();
        List<XcOrdersGoods> xcOrdersGoodsList = JSON.parseArray(orderDetail, XcOrdersGoods.class);
        for (XcOrdersGoods ordersGoods : xcOrdersGoodsList) {
            ordersGoods.setOrderId(orderID);
            xcOrdersGoodsMapper.insert(ordersGoods);
        }
        return xcOrders;
    }
    public XcOrders getOrderByBusinessId(String bussinessId){

        XcOrders xcOrders = xcOrdersMapper.selectOne(new LambdaQueryWrapper<XcOrders>()
                .eq(XcOrders::getOutBusinessId, bussinessId));
        return xcOrders;
    }
    @Override
    public XcPayRecord getPayRecordByPayno(String payNo) {
        XcPayRecord xcPayRecord = xcPayRecordMapper.selectOne(new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));
        return xcPayRecord;
    }

    @Override
    public PayRecordDto queryPayResult(String payNo) {
        XcPayRecord payRecord = getPayRecordByPayno(payNo);
        if (payRecord==null)
            XcException.cast("请重新获取二维码");

        if ("601002".equals(payRecord.getStatus())) {
            PayRecordDto payRecordDto = new PayRecordDto();
            BeanUtils.copyProperties(payRecord, payRecordDto);
            return payRecordDto;
        }
        PayStatusDto payStatusDto = queryPayResultFromAlipay(payNo);
        currentProxy.saveAliPayStatus( payStatusDto);
        //重新查询支付记录
        payRecord = getPayRecordByPayno(payNo);
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord, payRecordDto);
        return payRecordDto;


    }

    @Override
    public PayStatusDto queryPayResultFromAlipay(String payNo) {
        //========请求支付宝查询支付结果=============
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, "json", AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE); //获得初始化的AlipayClient
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", payNo);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                XcException.cast("请求支付查询查询失败");
            }
        } catch (AlipayApiException e) {
            log.error("请求支付宝查询支付结果异常:{}", e.toString(), e);
            XcException.cast("请求支付查询查询失败");
        }

        //获取支付结果
        String resultJson = response.getBody();
        //转map
        Map resultMap = JSON.parseObject(resultJson, Map.class);
        Map alipay_trade_query_response = (Map) resultMap.get("alipay_trade_query_response");
        //支付结果
        String trade_status = (String) alipay_trade_query_response.get("trade_status");
        String total_amount = (String) alipay_trade_query_response.get("total_amount");
        String trade_no = (String) alipay_trade_query_response.get("trade_no");
        //保存支付结果
        PayStatusDto payStatusDto = new PayStatusDto();
        payStatusDto.setOut_trade_no(payNo);
        payStatusDto.setTrade_status(trade_status);
        payStatusDto.setApp_id(APP_ID);
        payStatusDto.setTrade_no(trade_no);
        payStatusDto.setTotal_amount(total_amount);
        return payStatusDto;


    }

    @Override
    public void saveAliPayStatus(PayStatusDto payStatusDto) {

    }
}
