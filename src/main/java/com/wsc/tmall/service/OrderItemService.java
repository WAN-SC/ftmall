package com.wsc.tmall.service;

import com.wsc.tmall.dao.OrderItemDao;
import com.wsc.tmall.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="orderItems")
public class OrderItemService {

    @Autowired
    OrderItemDao orderItemDao;
    @Autowired
    ProductImageService productImageService;

    @Cacheable(key="'orderItems-one-'+ #p0")
    public OrderItem get(int id) {
        return orderItemDao.findOne(id);
    }

    public void fill(List<Order> orders){
        for(Order order : orders){
            fill(order);
        }
    }
    public void fill(Order order){
        List<OrderItem> ois = orderItemDao.findByOrderOrderByIdAsc(order);
        float total = 0;
        int totalNumber = 0;
        for (OrderItem oi : ois) {
            total += oi.getNumber()*oi.getProduct().getPromotePrice();
            totalNumber += oi.getNumber();
            productImageService.setFirstProductImage(oi.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(ois);
        order.setTotalNumber(totalNumber);
    }
    public int getSaleCount(Product product){
        List<OrderItem> ois = orderItemDao.findByProduct(product);
        int result = 0;
        for(OrderItem oi : ois){
            if(null!=oi.getOrder() && null!=oi.getOrder().getCreateDate())
                result+=oi.getNumber();
        }
        return result;
    }
    @Cacheable(key="'orderItems-uid-'+ #p0.id")
    public List<OrderItem> listByUser(User user){
        return orderItemDao.findByUserAndOrderIsNull(user);
    }
    @CacheEvict(allEntries=true)
    public void update(OrderItem oi){orderItemDao.save(oi);}
    @CacheEvict(allEntries=true)
    public void add(OrderItem oi){
        orderItemDao.save(oi);
    }
    @CacheEvict(allEntries=true)
    public void delete(int id){
        orderItemDao.delete(id);
    }
}
