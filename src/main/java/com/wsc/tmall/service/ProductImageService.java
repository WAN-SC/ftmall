package com.wsc.tmall.service;

import com.wsc.tmall.dao.ProductImageDao;
import com.wsc.tmall.pojo.OrderItem;
import com.wsc.tmall.pojo.Product;
import com.wsc.tmall.pojo.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="productImages")
public class ProductImageService {

    public static final String type_single = "single";
    public static final String type_detail = "detail";

    @Autowired
    ProductImageDao productImageDao;
    @Autowired
    ProductService productService;

    @CacheEvict(allEntries=true)
    public void add(ProductImage bean) {
        productImageDao.save(bean);

    }
    @CacheEvict(allEntries=true)
    public void delete(int id) {
        productImageDao.delete(id);
    }

    @Cacheable(key="'productImages-one-'+ #p0")
    public ProductImage get(int id) {
        return productImageDao.findOne(id);
    }

    @Cacheable(key="'productImages-single-pid-'+ #p0.id")
    public List<ProductImage> listSingleProductImages(Product product) {
        return productImageDao.findByProductAndTypeOrderByIdAsc(product, type_single);
    }
    @Cacheable(key="'productImages-detail-pid-'+ #p0.id")
    public List<ProductImage> listDetailProductImages(Product product) {
        return productImageDao.findByProductAndTypeOrderByIdAsc(product, type_detail);
    }

    public void setFirstProductImage(Product product){
        List<ProductImage> singleImages = listSingleProductImages(product);
        if(!singleImages.isEmpty())
            product.setFirstProductImage(singleImages.get(0));
        else
            product.setFirstProductImage(new ProductImage());
    }
    public void setFirstProductImage(List<Product> products){
        for (Product product : products) {
            setFirstProductImage(product);
        }
    }
    public void setFirstProdutImagesOnOrderItems(List<OrderItem> ois){
        for(OrderItem oi : ois){
            setFirstProductImage(oi.getProduct());
        }
    }
}
