package com.wsc.tmall.dao;

import com.wsc.tmall.pojo.Order;
import com.wsc.tmall.pojo.OrderItem;
import com.wsc.tmall.pojo.Product;
import com.wsc.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDao extends JpaRepository<OrderItem, Integer>{
    List<OrderItem> findByOrderOrderByIdAsc(Order order);
    List<OrderItem> findByProduct(Product product);
    List<OrderItem> findByUserAndOrderIsNull(User user);
}
