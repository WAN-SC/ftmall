package com.wsc.tmall.dao;

import com.wsc.tmall.pojo.Product;
import com.wsc.tmall.pojo.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewDao extends JpaRepository<Review,Integer>{
    List<Review> findByProductOrderByIdDesc(Product product);
    int countByProduct(Product product);
}
