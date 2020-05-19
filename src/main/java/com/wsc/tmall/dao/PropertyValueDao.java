package com.wsc.tmall.dao;

import com.wsc.tmall.pojo.Product;
import com.wsc.tmall.pojo.Property;
import com.wsc.tmall.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyValueDao extends JpaRepository<PropertyValue, Integer>{
    PropertyValue findByProductAndAndProperty(Product product, Property property);
    List<PropertyValue> findByProductOrderByIdAsc(Product product);
}