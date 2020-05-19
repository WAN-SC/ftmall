package com.wsc.tmall.service;

import com.wsc.tmall.dao.OrderDao;
import com.wsc.tmall.pojo.Order;
import com.wsc.tmall.pojo.OrderItem;
import com.wsc.tmall.pojo.User;
import com.wsc.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@CacheConfig(cacheNames="orders")
public class OrderService {

    @Autowired
    OrderDao orderDao;
    @Autowired
    OrderItemService orderItemService;

    @Cacheable(key="'orders-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Order> list(int start, int size, int navigatePages){
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        Page<Order> page = orderDao.findAll(pageable);
        return new Page4Navigator<>(page,navigatePages);
    }
    @CacheEvict(allEntries=true)
    public void update(Order order){
        orderDao.save(order);
    }
    public Order get(int id){
        return orderDao.getOne(id);
    }
    public void removeOrderFromOrderItem(List<Order> orders){
        for(Order order : orders){
            removeOrderFromOrderItem(order);
        }
    }
    public void removeOrderFromOrderItem(Order order){
        List<OrderItem> ois = order.getOrderItems();
        for(OrderItem oi : ois){
            oi.setOrder(null);
        }
    }
    @CacheEvict(allEntries=true)
    @Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
    public float add(Order order, List<OrderItem> ois){
        float total = 0;
        add(order);
        if(false)
            throw new RuntimeException();
        for(OrderItem oi : ois){
            oi.setOrder(order);
            orderItemService.update(oi);
            total+=oi.getProduct().getPromotePrice()*oi.getNumber();
        }
        return total;

    }
    @CacheEvict(allEntries=true)
    public void add(Order order){
        orderDao.save(order);
    }
    public List<Order> listByUserWithoutDelete(User user) {
        List<Order> orders = listByUserAndNotDeleted(user);
        orderItemService.fill(orders);
        return orders;
    }
    @Cacheable(key="'orders-uid-'+ #p0.id")
    public List<Order> listByUserAndNotDeleted(User user) {
        return orderDao.findByUserAndStatusNotOrderByIdDesc(user, Order.delete);
    }
    public void cacl(Order o) {
        List<OrderItem> orderItems = o.getOrderItems();
        float total = 0;
        for (OrderItem orderItem : orderItems) {
            total+=orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
        }
        o.setTotal(total);
    }
}
