package com.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.product.entity.ProductSku;

import java.util.List;

public interface ProductSkuService extends IService<ProductSku> {

    List<ProductSku> getBySpuId(Long spuId);

    ProductSku getBySkuCode(String skuCode);
}