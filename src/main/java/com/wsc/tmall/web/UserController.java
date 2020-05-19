package com.wsc.tmall.web;

import com.wsc.tmall.pojo.User;
import com.wsc.tmall.service.UserService;
import com.wsc.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/users")
    public Page4Navigator<User> list(@RequestParam(value="start",defaultValue = "0")int start, @RequestParam(value="size",defaultValue = "5")int size){
        start = start<0?0:start;
        System.out.println("qew");
        return userService.list(start,size,5);
    }
}
