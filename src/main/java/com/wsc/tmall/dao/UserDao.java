package com.wsc.tmall.dao;

import com.wsc.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface UserDao extends JpaRepository<User, Integer>{
    User findByName(String name);
    User getByNameAndPassword(String name,String password);
}