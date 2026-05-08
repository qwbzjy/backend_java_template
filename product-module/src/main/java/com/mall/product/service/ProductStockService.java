package com.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.product.entity.ProductStock;

public interface ProductStockService extends IService<ProductStock> {

    boolean lockStock(Long skuId, Integer quantity);

    boolean unlockStock(Long skuId, Integer quantity);

    boolean deductStock(Long skuId, Integer quantity);

    void initStock(Long skuId, Integer stock);
}