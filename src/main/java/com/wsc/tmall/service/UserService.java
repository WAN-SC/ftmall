package com.wsc.tmall.service;

import com.wsc.tmall.dao.UserDao;
import com.wsc.tmall.pojo.User;
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
@CacheConfig(cacheNames="users")
public class UserService {

    @Autowired
    UserDao userDao;

    @Cacheable(key="'users-page-'+#p0+ '-' + #p1")
    public Page4Navigator<User> list(int start, int size, int navigatePages){
        Sort sort = new Sort(Sort.Direction.ASC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        Page page = userDao.findAll(pageable);
        return new Page4Navigator<>(page,navigatePages);
    }
    public boolean isExist(String name){
        User user = getByName(name);
        return null!=user;
    }
    @CacheEvict(allEntries=true)
    public void add(User user){
        userDao.save(user);
    }
    @Cacheable(key="'users-one-name-'+ #p0 +'-password-'+ #p1")
    public User get(String name,String password){
        return userDao.getByNameAndPassword(name, password);
    }
    @Cacheable(key="'users-one-name-'+ #p0")
    public User getByName(String name){
        return userDao.findByName(name);
    }
}