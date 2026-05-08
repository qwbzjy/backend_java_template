package com.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.product.entity.ProductSku;
import com.mall.product.mapper.ProductSkuMapper;
import com.mall.product.service.ProductSkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSkuServiceImpl extends ServiceImpl<ProductSkuMapper, ProductSku> implements ProductSkuService {

    @Override
    public List<ProductSku> getBySpuId(Long spuId) {
        return lambdaQuery()
                .eq(ProductSku::getSpuId, spuId)
                .eq(ProductSku::getStatus, 1)
                .list();
    }

    @Override
    public ProductSku getBySkuCode(String skuCode) {
        return lambdaQuery()
                .eq(ProductSku::getSkuCode, skuCode)
                .one();
    }
}