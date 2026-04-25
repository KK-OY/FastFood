package com.sky.mapper;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    void add(Orders orders);

    void addDetail(List<OrderDetail> orderDetails);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    Orders getById(Long id);

    List<OrderDetail> getByIdOfDetail(Long id);

    List<OrderVO> GetHistoryOrders(String status);

    void cancelOrder(Long id);

    void again(List<ShoppingCart> shoppingCarts);

    List<Orders> GetOrdersPage(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO getOrdersStatus();

    void takeOrders(OrdersConfirmDTO ordersConfirmDTO);

    void refuseOrders(OrdersRejectionDTO ordersRejectionDTO);

    void adminCancelOrder(OrdersCancelDTO ordersCancelDTO);

    void sendOrder(Long id);

    void finishOrders(Long id);

    List<Orders> getStatusAndTime(LocalDateTime Time, Integer status);

    Integer countByCondition(LocalDateTime beginTime, LocalDateTime endTime, Object o);

    Integer geTodayOrders(LocalDate t,Integer s);

    Double getTodayOrderAmount(LocalDate t);

    Integer countByMap(Map map);

    Double sumByMap(Map map);
}
