package com.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.product.entity.ProductSpu;

import java.util.List;

public interface ProductSpuService extends IService<ProductSpu> {

    List<ProductSpu> getByCategoryId(Long categoryId);

    void updateStock(Long id, Integer quantity);
}