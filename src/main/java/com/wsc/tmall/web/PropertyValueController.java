package com.wsc.tmall.web;

import com.wsc.tmall.pojo.Product;
import com.wsc.tmall.pojo.PropertyValue;
import com.wsc.tmall.service.ProductService;
import com.wsc.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropertyValueController {

    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ProductService productService;

    @GetMapping("/products/{pid}/propertyValues")
    public List<PropertyValue> list(@PathVariable("pid")int pid){
        Product product = productService.get(pid);
        propertyValueService.init(product);
        List<PropertyValue> beans = propertyValueService.list(product);
        return beans;
    }
    @PutMapping("/propertyValues")
    public PropertyValue update(@RequestBody PropertyValue bean){
        propertyValueService.update(bean);
        return bean;
    }
}
