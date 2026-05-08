package com.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.product.entity.ProductCategory;

import java.util.List;

public interface ProductCategoryService extends IService<ProductCategory> {

    List<ProductCategory> getTree();
}