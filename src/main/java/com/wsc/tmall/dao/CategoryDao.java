package com.wsc.tmall.dao;

import com.wsc.tmall.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDao extends JpaRepository<Category,Integer>{
}
