package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.*;

public interface OrderService {
    OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    OrderVO GetOrderById(Long id);

    PageResult GetHistoryOrders(String page, String pageSize, String status);


    void cancelOrder(Long id);

    void again(Long id);

    PageResult GetOrdersPage(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO getOrdersStatus();

    void takeOrders(OrdersConfirmDTO ordersConfirmDTO);

    void refuseOrders(OrdersRejectionDTO ordersRejectionDTO);

    void adminCancelOrders(OrdersCancelDTO ordersCancelDTO);

    void sendOrders(Long id);

    void finishOrders(Long id);

    OrderOverViewVO getOrderOverView();

    void urgeOrders(Long id);
}
