package com.wsc.tmall.web;

import com.wsc.tmall.comparator.*;
import com.wsc.tmall.pojo.*;
import com.wsc.tmall.service.*;
import com.wsc.tmall.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ForeRESTController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;

    @GetMapping("/forehome")
    public Object home(){
        List<Category> cs = categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }
    @PostMapping("/foreregister")
    public Object register(@RequestBody User user){
        String name = HtmlUtils.htmlEscape(user.getName());
        String password = user.getPassword();
        if(userService.isExist(name))
            return Result.fail("用户名已经被使用,不能使用");
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String algorithmName = "md5";
        String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();
        user.setSalt(salt);
        user.setPassword(encodedPassword);
        userService.add(user);
        return Result.success();
    }
    @PostMapping("/forelogin")
    public Object login(@RequestBody User user, HttpSession session){
        String name = HtmlUtils.htmlEscape(user.getName());
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(name, user.getPassword());
        try{
            subject.login(token);
            User u = userService.getByName(name);
            session.setAttribute("user",u);
            return Result.success();
        }catch (AuthenticationException e){
            return Result.fail("账号或密码错误");
        }
    }
    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid")int pid){
        Product product = productService.get(pid);

        List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);

        List<PropertyValue> pvs = propertyValueService.list(product);
        List<Review> reviews = reviewService.list(product);
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProductImage(product);
        Map<String,Object> map= new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);

        return Result.success(map);
    }
    @GetMapping("forecheckLogin")
    public Object checkLogin(){
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated())
            return Result.success();
        else
            return Result.fail("未登录");
    }
    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable int cid,String sort){
        Category c = categoryService.get(cid);
        productService.fill(c);
        productService.setSaleAndReviewNumber(c.getProducts());
        categoryService.removeCategoryFromProduct(c);
        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(c.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(c.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(c.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(c.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(c.getProducts(),new ProductAllComparator());
                    break;
            }
        }
        return c;
    }
    @PostMapping("foresearch")
    public Object search( String keyword){
        if(null == keyword)
            keyword = "";
        List<Product> ps= productService.search(keyword,0,20);
        productImageService.setFirstProductImage(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }
    @GetMapping("/forebuyone")
    public Object buyone(int pid, int num, HttpSession session){
        return buyoneAndAddCart(pid,num,session);
    }
    private int buyoneAndAddCart(int pid, int num, HttpSession session){
        Product product = productService.get(pid);
        int oiid = 0;
        User user = (User)session.getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemService.listByUser(user);
        for(OrderItem oi : ois){
            if(product.getId() == oi.getProduct().getId()){
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }
        if(!found){
            OrderItem oi = new OrderItem();
            oi.setProduct(product);
            oi.setUser(user);
            oi.setNumber(num);
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        return oiid;
    }
    @GetMapping("/forebuy")
    public Object buy(String[] oiid,HttpSession session){
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;

        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi= orderItemService.get(id);
            total +=oi.getProduct().getPromotePrice()*oi.getNumber();
            orderItems.add(oi);
        }

        productImageService.setFirstProdutImagesOnOrderItems(orderItems);

        session.setAttribute("ois", orderItems);

        Map<String,Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);
    }
    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid,num,session);
        return Result.success();
    }
    @GetMapping("forecart")
    public Object cart(HttpSession session){
        User user = (User)session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user);
        productImageService.setFirstProdutImagesOnOrderItems(ois);
        return ois;
    }
    @GetMapping("forechangeOrderItem")
    public Object changeOrderItem( HttpSession session, int pid, int num){
        User user = (User)session.getAttribute("user");
        if(null == user)
            return Result.fail("未登录");
        List<OrderItem> ois = orderItemService.listByUser(user);
        for(OrderItem oi : ois){
            if(pid == oi.getProduct().getId()){
                oi.setNumber(num);
                orderItemService.update(oi);
                break;
            }
        }
        return Result.success();
    }
    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session,int oiid){
        User user =(User)  session.getAttribute("user");
        if(null == user)
            Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }
    @PostMapping("forecreateOrder")
    public Object createOrder(@RequestBody Order order,HttpSession session){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(order.waitPay);
        List<OrderItem> ois= (List<OrderItem>)  session.getAttribute("ois");
        float total =orderService.add(order,ois);
        Map<String,Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);
    }
    @GetMapping("forepayed")
    public Object payed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(order.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }
    @GetMapping("forebought")
    public Object bought(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        List<Order> os= orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(os);
        return os;
    }
    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.cacl(o);
        orderService.removeOrderFromOrderItem(o);
        return o;
    }
    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed( int oid) {
        Order o = orderService.get(oid);
        o.setStatus(o.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return Result.success();
    }
    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid){
        Order o = orderService.get(oid);
        o.setStatus(o.delete);
        orderService.update(o);
        return Result.success();
    }
    @GetMapping("forereview")
    public Object review(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.removeOrderFromOrderItem(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(p);
        productService.setSaleAndReviewNumber(p);
        Map<String,Object> map = new HashMap<>();
        map.put("p", p);
        map.put("o", o);
        map.put("reviews", reviews);

        return Result.success(map);
    }
    @PostMapping("foredoreview")
    public Object doreview( HttpSession session,int oid,int pid,String content) {
        Order o = orderService.get(oid);
        o.setStatus(o.finish);
        orderService.update(o);

        Product p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user =(User)  session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(user);
        reviewService.add(review);
        return Result.success();
    }
}