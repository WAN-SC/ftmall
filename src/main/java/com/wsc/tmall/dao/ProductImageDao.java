package com.wsc.tmall.dao;

import com.wsc.tmall.pojo.Product;
import com.wsc.tmall.pojo.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageDao extends JpaRepository<ProductImage,Integer>{
    public List<ProductImage> findByProductAndTypeOrderByIdAsc(Product product, String type);
}
