package com.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.product.entity.ProductStock;
import com.mall.product.mapper.ProductStockMapper;
import com.mall.product.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductStockServiceImpl extends ServiceImpl<ProductStockMapper, ProductStock> implements ProductStockService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean lockStock(Long skuId, Integer quantity) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            ProductStock stock = getBySkuId(skuId);
            if (stock == null || stock.getStock() < quantity) {
                return false;
            }

            int rows = baseMapper.update(null,
                    new LambdaUpdateWrapper<ProductStock>()
                            .eq(ProductStock::getSkuId, skuId)
                            .eq(ProductStock::getVersion, stock.getVersion())
                            .set(ProductStock::getStock, stock.getStock() - quantity)
                            .set(ProductStock::getLockStock, stock.getLockStock() + quantity)
                            .set(ProductStock::getVersion, stock.getVersion() + 1));

            if (rows > 0) {
                log.info("锁定库存成功: skuId={}, quantity={}", skuId, quantity);
                return true;
            }
            log.warn("锁定库存失败，重试: {}", i + 1);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlockStock(Long skuId, Integer quantity) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            ProductStock stock = getBySkuId(skuId);
            if (stock == null || stock.getLockStock() < quantity) {
                return false;
            }

            int rows = baseMapper.update(null,
                    new LambdaUpdateWrapper<ProductStock>()
                            .eq(ProductStock::getSkuId, skuId)
                            .eq(ProductStock::getVersion, stock.getVersion())
                            .set(ProductStock::getStock, stock.getStock() + quantity)
                            .set(ProductStock::getLockStock, stock.getLockStock() - quantity)
                            .set(ProductStock::getVersion, stock.getVersion() + 1));

            if (rows > 0) {
                log.info("解锁库存成功: skuId={}, quantity={}", skuId, quantity);
                return true;
            }
            log.warn("解锁库存失败，重试: {}", i + 1);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(Long skuId, Integer quantity) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            ProductStock stock = getBySkuId(skuId);
            if (stock == null || stock.getLockStock() < quantity) {
                return false;
            }

            int rows = baseMapper.update(null,
                    new LambdaUpdateWrapper<ProductStock>()
                            .eq(ProductStock::getSkuId, skuId)
                            .eq(ProductStock::getVersion, stock.getVersion())
                            .set(ProductStock::getLockStock, stock.getLockStock() - quantity)
                            .set(ProductStock::getSoldStock, stock.getSoldStock() + quantity)
                            .set(ProductStock::getVersion, stock.getVersion() + 1));

            if (rows > 0) {
                log.info("扣减库存成功: skuId={}, quantity={}", skuId, quantity);
                return true;
            }
            log.warn("扣减库存失败，重试: {}", i + 1);
        }
        return false;
    }

    @Override
    public void initStock(Long skuId, Integer stock) {
        ProductStock exist = getBySkuId(skuId);
        if (exist != null) {
            return;
        }
        ProductStock newStock = new ProductStock();
        newStock.setSkuId(skuId);
        newStock.setStock(stock);
        newStock.setLockStock(0);
        newStock.setSoldStock(0);
        newStock.setVersion(0);
        save(newStock);
    }

    private ProductStock getBySkuId(Long skuId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<ProductStock>()
                .eq(ProductStock::getSkuId, skuId));
    }
}