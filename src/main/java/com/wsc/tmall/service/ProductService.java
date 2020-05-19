package com.wsc.tmall.service;

import com.wsc.tmall.dao.ProductDao;
import com.wsc.tmall.pojo.Category;
import com.wsc.tmall.pojo.Product;
import com.wsc.tmall.util.Page4Navigator;
import com.wsc.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames="products")
public class ProductService  {

    @Autowired
    ProductDao productDao;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    @CacheEvict(allEntries=true)
    public void add(Product bean) {
        productDao.save(bean);
    }

    @CacheEvict(allEntries=true)
    public void delete(int id) {
        productDao.delete(id);
    }

    @Cacheable(key="'products-one-'+ #p0")
    public Product get(int id) {
        return productDao.findOne(id);
    }

    @CacheEvict(allEntries=true)
    public void update(Product bean) {
        productDao.save(bean);
    }

    @Cacheable(key="'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Product> list(int cid, int start, int size,int navigatePages) {
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Product> pageFromJPA =productDao.findByCategory(category,pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    @Cacheable(key="'products-cid-'+ #p0.id")
    public List<Product> listByCategory(Category category){
        return productDao.findByCategoryOrderById(category);
    }

    public void fill(Category category){
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = productService.listByCategory(category);
        productImageService.setFirstProductImage(products);
        category.setProducts(products);
    }
    public void fill(List<Category> cs){
        for(Category c : cs){
            fill(c);
        }
    }
    public void fillByRow(List<Category> cs){
        int productNumberEachRow = 8;
        for(Category c : cs){
            List<Product> products =  c.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for(int i=0; i<products.size(); i+=productNumberEachRow){
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                List<Product> productsOfEachRow = products.subList(i,size);
                productsByRow.add(productsOfEachRow);
            }
            c.setProductsByRow(productsByRow);
        }
    }

    public void setSaleAndReviewNumber(Product product){
        int reviewCount = reviewService.getCount(product);
        int saleCount = orderItemService.getSaleCount(product);
        product.setReviewCount(reviewCount);
        product.setSaleCount(saleCount);
    }
    public void setSaleAndReviewNumber(List<Product> products){
        for(Product product : products){
            setSaleAndReviewNumber(product);
        }
    }

    public List<Product> search(String keyword, int start, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        List<Product> products =productDao.findByNameLike("%"+keyword+"%",pageable);
        return products;
    }

}
