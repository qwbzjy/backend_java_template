package com.mall.api.controller.product;

import com.mall.api.dto.product.CreateCategoryDTO;
import com.mall.api.dto.product.CreateSkuDTO;
import com.mall.api.dto.product.CreateSpuDTO;
import com.mall.product.entity.ProductCategory;
import com.mall.product.entity.ProductSku;
import com.mall.product.entity.ProductSpu;
import com.mall.product.service.ProductCategoryService;
import com.mall.product.service.ProductSkuService;
import com.mall.product.service.ProductSpuService;
import com.mall.product.service.ProductStockService;
import com.mall.api.vo.product.CategoryVO;
import com.mall.api.vo.product.SkuVO;
import com.mall.api.vo.product.SpuVO;
import com.mall.common.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "商品管理")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductSpuService productSpuService;
    private final ProductSkuService productSkuService;
    private final ProductCategoryService productCategoryService;
    private final ProductStockService productStockService;

    // ========== 分类管理 ==========

    @Operation(summary = "获取分类树")
    @GetMapping("/category/tree")
    public Result<List<CategoryVO>> getCategoryTree() {
        List<ProductCategory> tree = productCategoryService.getTree();
        List<CategoryVO> voList = convertCategoryTree(tree);
        return Result.success(voList);
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/category/{id}")
    public Result<CategoryVO> getCategory(@PathVariable Long id) {
        ProductCategory category = productCategoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return Result.success(vo);
    }

    @Operation(summary = "创建分类")
    @PostMapping("/category")
    public Result<Void> createCategory(@Valid @RequestBody CreateCategoryDTO dto) {
        ProductCategory category = new ProductCategory();
        category.setName(dto.getName());
        category.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        category.setLevel(dto.getLevel() != null ? dto.getLevel() : 1);
        category.setSort(dto.getSort() != null ? dto.getSort() : 0);
        category.setIcon(dto.getIcon());
        category.setStatus(1);
        productCategoryService.save(category);
        return Result.success();
    }

    @Operation(summary = "更新分类")
    @PutMapping("/category/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody CreateCategoryDTO dto) {
        ProductCategory category = productCategoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        category.setName(dto.getName());
        if (dto.getParentId() != null) {
            category.setParentId(dto.getParentId());
        }
        if (dto.getSort() != null) {
            category.setSort(dto.getSort());
        }
        if (dto.getIcon() != null) {
            category.setIcon(dto.getIcon());
        }
        productCategoryService.updateById(category);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/category/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        long childCount = productCategoryService.lambdaQuery()
                .eq(ProductCategory::getParentId, id)
                .count();
        if (childCount > 0) {
            return Result.error("存在子分类，无法删除");
        }
        productCategoryService.removeById(id);
        return Result.success();
    }

    // ========== SPU管理 ==========

    @Operation(summary = "获取SPU列表")
    @GetMapping("/spu")
    public Result<List<SpuVO>> getSpuList(@RequestParam(required = false) Long categoryId) {
        List<ProductSpu> list;
        if (categoryId != null) {
            list = productSpuService.getByCategoryId(categoryId);
        } else {
            list = productSpuService.lambdaQuery()
                    .eq(ProductSpu::getStatus, 1)
                    .orderByDesc(ProductSpu::getCreateTime)
                    .list();
        }
        List<SpuVO> voList = list.stream().map(spu -> {
            SpuVO vo = new SpuVO();
            BeanUtils.copyProperties(spu, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(voList);
    }

    @Operation(summary = "获取SPU详情")
    @GetMapping("/spu/{id}")
    public Result<SpuVO> getSpu(@PathVariable Long id) {
        ProductSpu spu = productSpuService.getById(id);
        if (spu == null) {
            return Result.error("商品不存在");
        }
        SpuVO vo = new SpuVO();
        BeanUtils.copyProperties(spu, vo);

        List<ProductSku> skuList = productSkuService.getBySpuId(id);
        List<SkuVO> skuVoList = skuList.stream().map(sku -> {
            SkuVO skuVo = new SkuVO();
            BeanUtils.copyProperties(sku, skuVo);
            return skuVo;
        }).collect(Collectors.toList());
        vo.setSkus(skuVoList);

        return Result.success(vo);
    }

    @Operation(summary = "创建SPU")
    @PostMapping("/spu")
    public Result<Long> createSpu(@Valid @RequestBody CreateSpuDTO dto) {
        ProductSpu spu = new ProductSpu();
        BeanUtils.copyProperties(dto, spu);
        spu.setStock(0);
        spu.setSales(0);
        spu.setStatus(1);
        productSpuService.save(spu);
        return Result.success(spu.getId());
    }

    @Operation(summary = "更新SPU")
    @PutMapping("/spu/{id}")
    public Result<Void> updateSpu(@PathVariable Long id, @Valid @RequestBody CreateSpuDTO dto) {
        ProductSpu spu = productSpuService.getById(id);
        if (spu == null) {
            return Result.error("商品不存在");
        }
        BeanUtils.copyProperties(dto, spu);
        productSpuService.updateById(spu);
        return Result.success();
    }

    @Operation(summary = "上架/下架SPU")
    @PutMapping("/spu/{id}/status")
    public Result<Void> updateSpuStatus(@PathVariable Long id, @RequestParam Integer status) {
        productSpuService.lambdaUpdate()
                .eq(ProductSpu::getId, id)
                .set(ProductSpu::getStatus, status)
                .update();
        return Result.success();
    }

    @Operation(summary = "删除SPU")
    @DeleteMapping("/spu/{id}")
    public Result<Void> deleteSpu(@PathVariable Long id) {
        productSpuService.removeById(id);
        return Result.success();
    }

    // ========== SKU管理 ==========

    @Operation(summary = "获取SKU列表")
    @GetMapping("/sku")
    public Result<List<SkuVO>> getSkuList(@RequestParam Long spuId) {
        List<ProductSku> list = productSkuService.getBySpuId(spuId);
        List<SkuVO> voList = list.stream().map(sku -> {
            SkuVO vo = new SkuVO();
            BeanUtils.copyProperties(sku, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(voList);
    }

    @Operation(summary = "获取SKU详情")
    @GetMapping("/sku/{id}")
    public Result<SkuVO> getSku(@PathVariable Long id) {
        ProductSku sku = productSkuService.getById(id);
        if (sku == null) {
            return Result.error("SKU不存在");
        }
        SkuVO vo = new SkuVO();
        BeanUtils.copyProperties(sku, vo);
        return Result.success(vo);
    }

    @Operation(summary = "创建SKU")
    @PostMapping("/sku")
    public Result<Long> createSku(@Valid @RequestBody CreateSkuDTO dto) {
        ProductSku sku = new ProductSku();
        BeanUtils.copyProperties(dto, sku);
        sku.setSales(0);
        sku.setStatus(1);
        productSkuService.save(sku);

        productStockService.initStock(sku.getId(), dto.getStock());
        productSpuService.updateStock(dto.getSpuId(), dto.getStock());

        return Result.success(sku.getId());
    }

    @Operation(summary = "更新SKU")
    @PutMapping("/sku/{id}")
    public Result<Void> updateSku(@PathVariable Long id, @Valid @RequestBody CreateSkuDTO dto) {
        ProductSku sku = productSkuService.getById(id);
        if (sku == null) {
            return Result.error("SKU不存在");
        }
        BeanUtils.copyProperties(dto, sku);
        productSkuService.updateById(sku);
        return Result.success();
    }

    @Operation(summary = "删除SKU")
    @DeleteMapping("/sku/{id}")
    public Result<Void> deleteSku(@PathVariable Long id) {
        productSkuService.removeById(id);
        return Result.success();
    }

    // ========== 库存管理 ==========

    @Operation(summary = "锁定库存")
    @PostMapping("/stock/lock")
    public Result<Void> lockStock(@RequestParam Long skuId, @RequestParam Integer quantity) {
        boolean success = productStockService.lockStock(skuId, quantity);
        if (!success) {
            return Result.error("库存不足或锁定失败");
        }
        return Result.success();
    }

    @Operation(summary = "解锁库存")
    @PostMapping("/stock/unlock")
    public Result<Void> unlockStock(@RequestParam Long skuId, @RequestParam Integer quantity) {
        boolean success = productStockService.unlockStock(skuId, quantity);
        if (!success) {
            return Result.error("解锁失败");
        }
        return Result.success();
    }

    @Operation(summary = "扣减库存")
    @PostMapping("/stock/deduct")
    public Result<Void> deductStock(@RequestParam Long skuId, @RequestParam Integer quantity) {
        boolean success = productStockService.deductStock(skuId, quantity);
        if (!success) {
            return Result.error("扣减失败");
        }
        return Result.success();
    }

    // ========== 转换方法 ==========

    private List<CategoryVO> convertCategoryTree(List<ProductCategory> categories) {
        return categories.stream().map(c -> {
            CategoryVO vo = new CategoryVO();
            vo.setId(c.getId());
            vo.setName(c.getName());
            vo.setParentId(c.getParentId());
            vo.setLevel(c.getLevel());
            vo.setSort(c.getSort());
            vo.setIcon(c.getIcon());
            vo.setStatus(c.getStatus());
            vo.setCreateTime(c.getCreateTime());
            if (c.getChildren() != null && !c.getChildren().isEmpty()) {
                vo.setChildren(convertCategoryTree(c.getChildren()));
            } else {
                vo.setChildren(new ArrayList<>());
            }
            return vo;
        }).collect(Collectors.toList());
    }
}