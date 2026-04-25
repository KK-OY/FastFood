package com.sky.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.BaseException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShopCartMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.BaseContext;
import com.sky.vo.*;
import com.sky.webServe.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ShopCartMapper shopCartMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WebSocketServer webSocketServer;
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO) {
        //查地址与用户是否存在
        AddressBook address = addressBookMapper.findAddress(ordersSubmitDTO.getAddressBookId());
        if (address == null ) {
          throw  new BaseException("地址异常");
        }

        List<ShoppingCart> shopCart = shopCartMapper.getShopCart(BaseContext.getCurrentId());
        if (shopCart == null || shopCart.isEmpty() ) {
            throw  new BaseException("购物车异常");
        }

        //插入数据向订单表
        Orders orders =new Orders();
        AddressBook address1 = addressBookMapper.findAddress(ordersSubmitDTO.getAddressBookId());
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(address.getPhone());
        orders.setConsignee(address.getConsignee());
        orders.setAddress(address1.getProvinceName()+address1.getDistrictName());
        orders.setUserId(BaseContext.getCurrentId());

        orderMapper.add(orders);
        //插入n条数据向订单明细表
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart :shopCart){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }

        orderMapper.addDetail(orderDetails);

        //清空购物车
        shopCartMapper.cleanShopCart(BaseContext.getCurrentId());

        //装箱准备发给前端
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();

            return orderSubmitVO;
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//
//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"))

        OrderPaymentVO vo = OrderPaymentVO.builder()
                .packageStr("id")
                .paySign("wx")
                .timeStamp(String.valueOf(System.currentTimeMillis()))
                .signType("yb8p")
                .nonceStr(String.valueOf(System.currentTimeMillis()))
                .build();
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        //向管理系统推送消息使用websock

        Map mp = new HashMap<>();
        mp.put("type",1);
        mp.put("orderId",ordersDB.getId());
        mp.put("content","订单号"+outTradeNo);

        String jsonString = JSONObject.toJSONString(mp);
        webSocketServer.sendToAllClient(jsonString);
    }

    @Override
    public OrderVO GetOrderById(Long id) {
        Orders oldorder = orderMapper.getById(id);
        List<OrderDetail> byIdOfDetail = orderMapper.getByIdOfDetail(id);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(oldorder,orderVO);

        orderVO.setOrderDetailList(byIdOfDetail);
        orderVO.setOrderDishes("你好哈哈");
        return orderVO;
    }

    @Override
    public PageResult GetHistoryOrders(String page, String pageSize, String status) {
        try {
        PageHelper.startPage(Integer.parseInt(page),Integer.parseInt(pageSize));

            List<OrderVO> orderVOS = orderMapper.GetHistoryOrders(status);

            for(OrderVO orderVO : orderVOS){
                orderVO.setOrderDetailList(orderMapper.getByIdOfDetail(orderVO.getId()));
            }

            Page<OrderVO> p = (Page<OrderVO>) orderVOS;
            return new PageResult(p.getTotal(),p.getResult());
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void cancelOrder(Long id) {
        orderMapper.cancelOrder(id);
    }

    @Override
    public void again(Long id) {
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        List<OrderDetail> byIdOfDetail = orderMapper.getByIdOfDetail(id);

        for(OrderDetail o : byIdOfDetail){
            ShoppingCart s = ShoppingCart.builder()
                    .name(o.getName())
                    .amount(o.getAmount())
                    .image(o.getImage())
                    .dishFlavor(o.getDishFlavor())
                    .dishId(o.getDishId())
                    .number(o.getNumber())
                    .userId(BaseContext.getCurrentId())
                    .setmealId(o.getSetmealId())
                    .createTime(LocalDateTime.now())
                    .build();

            shoppingCarts.add(s);
        }

        orderMapper.again(shoppingCarts);
    }

    @Override
    public PageResult GetOrdersPage(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        List<Orders> orders = orderMapper.GetOrdersPage(ordersPageQueryDTO);

        Page<Orders> p = (Page<Orders>) orders;

        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public OrderStatisticsVO getOrdersStatus() {
        OrderStatisticsVO ordersStatus = orderMapper.getOrdersStatus();
        return ordersStatus;
    }

    @Override
    public void takeOrders(OrdersConfirmDTO ordersConfirmDTO) {
        orderMapper.takeOrders(ordersConfirmDTO);
    }

    @Override
    public void refuseOrders(OrdersRejectionDTO ordersRejectionDTO) {
        orderMapper.refuseOrders(ordersRejectionDTO);
    }

    @Override
    public void adminCancelOrders(OrdersCancelDTO ordersCancelDTO) {
        orderMapper.adminCancelOrder(ordersCancelDTO);
    }

    @Override
    public void sendOrders(Long id) {
        orderMapper.sendOrder(id);
    }

    @Override
    public void finishOrders(Long id) {
        orderMapper.finishOrders(id);
    }

    @Override
    public OrderOverViewVO getOrderOverView() {
        /**
         * 查询订单管理数据
         *
         * @return
         */

            Map map = new HashMap();
            map.put("begin", LocalDateTime.now().with(LocalTime.MIN));
            map.put("status", Orders.TO_BE_CONFIRMED);

            //待接单
            Integer waitingOrders = orderMapper.countByMap(map);

            //待派送
            map.put("status", Orders.CONFIRMED);
            Integer deliveredOrders = orderMapper.countByMap(map);

            //已完成
            map.put("status", Orders.COMPLETED);
            Integer completedOrders = orderMapper.countByMap(map);

            //已取消
            map.put("status", Orders.CANCELLED);
            Integer cancelledOrders = orderMapper.countByMap(map);

            //全部订单
            map.put("status", null);
            Integer allOrders = orderMapper.countByMap(map);

            return OrderOverViewVO.builder()
                    .waitingOrders(waitingOrders)
                    .deliveredOrders(deliveredOrders)
                    .completedOrders(completedOrders)
                    .cancelledOrders(cancelledOrders)
                    .allOrders(allOrders)
                    .build();
        }
    }

