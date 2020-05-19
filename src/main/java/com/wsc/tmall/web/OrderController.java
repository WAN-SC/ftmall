package com.wsc.tmall.web;

import com.wsc.tmall.pojo.Order;
import com.wsc.tmall.service.OrderItemService;
import com.wsc.tmall.service.OrderService;
import com.wsc.tmall.util.Page4Navigator;
import com.wsc.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;

    @GetMapping("/orders")
    public Page4Navigator<Order> list(@RequestParam(value = "start",defaultValue = "0")int start, @RequestParam(value = "size",defaultValue = "5")int size){
        start = start<0?0:start;
        Page4Navigator<Order> page = orderService.list(start,size,5);
        orderItemService.fill(page.getContent());
        orderService.removeOrderFromOrderItem(page.getContent());
        return page;
    }
    @PutMapping("/deliveryOrder/{id}")
    public Page4Navigator<Order> deliveryOrder(@PathVariable("id")int id){
        System.out.println("qwe");
        Order o = orderService.get(id);
        o.setDeliveryDate(new Date());
        o.setStatus(o.waitConfirm);
        orderService.update(o);
        return list(0,5);

    }
}
