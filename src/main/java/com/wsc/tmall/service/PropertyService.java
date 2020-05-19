package com.wsc.tmall.service;

import com.wsc.tmall.dao.PropertyDao;
import com.wsc.tmall.pojo.Category;
import com.wsc.tmall.pojo.Property;
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

import java.util.List;

@Service
@CacheConfig(cacheNames="properties")
public class PropertyService {

    @Autowired
    PropertyDao propertyDao;
    @Autowired
    CategoryService categoryService;

    @CacheEvict(allEntries=true)
    public void add(Property p){
        propertyDao.save(p);
    }
    @CacheEvict(allEntries=true)
    public void delete(int id){
        propertyDao.delete(id);
    }
    @CacheEvict(allEntries=true)
    public void update(Property p){
        propertyDao.save(p);
    }
    @Cacheable(key="'properties-one-'+ #p0")
    public Property get(int id){
        return propertyDao.getOne(id);
    }
    @Cacheable(key="'properties-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Property> list(int cid, int start, int size ,int navigatePages){
        Category c = categoryService.get(cid);
        Sort s = new Sort(Sort.Direction.ASC,"id");
        Pageable pageable = new PageRequest(start,size,s);
        Page<Property> page = propertyDao.findByCategory(c,pageable);
        return new Page4Navigator<>(page, navigatePages);
    }
    @Cacheable(key="'properties-cid-'+ #p0.id")
    public List<Property> listByCategory(Category category){
        return propertyDao.findByCategory(category);
    }
}
