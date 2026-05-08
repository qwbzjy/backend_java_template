package com.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.product.entity.ProductSpu;
import com.mall.product.mapper.ProductSpuMapper;
import com.mall.product.service.ProductSpuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSpuServiceImpl extends ServiceImpl<ProductSpuMapper, ProductSpu> implements ProductSpuService {

    @Override
    public List<ProductSpu> getByCategoryId(Long categoryId) {
        return lambdaQuery()
                .eq(ProductSpu::getCategoryId, categoryId)
                .eq(ProductSpu::getStatus, 1)
                .orderByDesc(ProductSpu::getCreateTime)
                .list();
    }

    @Override
    public void updateStock(Long id, Integer quantity) {
        lambdaUpdate()
                .eq(ProductSpu::getId, id)
                .setSql("stock = stock + " + quantity)
                .update();
    }
}