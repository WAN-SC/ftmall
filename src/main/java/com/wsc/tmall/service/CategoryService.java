package com.wsc.tmall.service;

import com.wsc.tmall.dao.CategoryDao;
import com.wsc.tmall.pojo.Category;
import com.wsc.tmall.pojo.Product;
import com.wsc.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="categories")
public class CategoryService {

    @Autowired
    CategoryDao categoryDao;

    public List<Category> list(){
        Sort sort = new Sort(Sort.Direction.ASC,"id");
        return categoryDao.findAll(sort);
    }
    public Page4Navigator<Category> list(int start, int size, int navigatePages){
        Sort sort = new Sort(Sort.Direction.ASC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        Page pageFromJPA = categoryDao.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }
    @CacheEvict(allEntries=true)
//  @CachePut(key="'category-one-'+ #p0")
    public void add(Category bean){
        categoryDao.save(bean);
    }
    @CacheEvict(allEntries=true)
//  @CacheEvict(key="'category-one-'+ #p0")
    public void delete(int id){
        categoryDao.delete(id);
    }
    public Category get(int id){
        Category category = categoryDao.findOne(id);
        return category;
    }
    @CacheEvict(allEntries=true)
//  @CachePut(key="'category-one-'+ #p0")
    public void update(Category bean){
        categoryDao.save(bean);
    }
    public void removeCategoryFromProduct(Category category){
        List<Product> products = category.getProducts();
        for(Product product : products){
            if(null != product)
                product.setCategory(null);
        }
        List<List<Product>> productsByRow = category.getProductsByRow();
        if(null != productsByRow){
            for(List<Product> ps : productsByRow){
                for(Product p : ps){
                    p.setCategory(null);
                }
            }
        }
    }
    public void removeCategoryFromProduct(List<Category> cs){
        for(Category category : cs){
            removeCategoryFromProduct(category);
        }
    }

}