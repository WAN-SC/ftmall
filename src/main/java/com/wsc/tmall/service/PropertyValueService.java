package com.wsc.tmall.service;

import com.wsc.tmall.dao.PropertyValueDao;
import com.wsc.tmall.pojo.Product;
import com.wsc.tmall.pojo.Property;
import com.wsc.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="propertyValues")
public class PropertyValueService {

    @Autowired
    PropertyValueDao propertyValueDao;
    @Autowired
    PropertyService propertyService;

    public void init(Product product){
        List<Property> propertys = propertyService.listByCategory(product.getCategory());
        for (Property property : propertys) {
            PropertyValue propertyValue = propertyValueDao.findByProductAndAndProperty(product, property);
            if(null == propertyValue){
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDao.save(propertyValue);
            }
        }
    }
    @Cacheable(key="'propertyValues-pid-'+ #p0.id")
    public List<PropertyValue> list(Product product){
        return propertyValueDao.findByProductOrderByIdAsc(product);
    }
    @CacheEvict(allEntries=true)
    public void update(PropertyValue bean){
        propertyValueDao.save(bean);
    }
}