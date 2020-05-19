package com.wsc.tmall.service;

import com.wsc.tmall.dao.ReviewDao;
import com.wsc.tmall.pojo.Product;
import com.wsc.tmall.pojo.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="reviews")
public class ReviewService {

    @Autowired
    ReviewDao reviewDao;
    @Autowired
    ProductService productService;

    @CacheEvict(allEntries=true)
    public void add(Review review){
        reviewDao.save(review);
    }
    @Cacheable(key="'reviews-pid-'+ #p0.id")
    public List<Review> list(Product product){
        return reviewDao.findByProductOrderByIdDesc(product);
    }
    @Cacheable(key="'reviews-count-pid-'+ #p0.id")
    public int getCount(Product product){
        return reviewDao.countByProduct(product);
    }
}
