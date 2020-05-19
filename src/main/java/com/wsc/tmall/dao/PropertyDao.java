package com.wsc.tmall.dao;

import com.wsc.tmall.pojo.Category;
import com.wsc.tmall.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PropertyDao extends JpaRepository<Property,Integer> {
    Page<Property> findByCategory(Category c, Pageable page);
    List<Property> findByCategory(Category category);
}
