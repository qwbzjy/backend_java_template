package com.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.product.entity.ProductCategory;
import com.mall.product.mapper.ProductCategoryMapper;
import com.mall.product.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Override
    public List<ProductCategory> getTree() {
        List<ProductCategory> all = lambdaQuery()
                .eq(ProductCategory::getStatus, 1)
                .orderByAsc(ProductCategory::getSort)
                .list();

        Map<Long, List<ProductCategory>> childrenMap = all.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() > 0)
                .collect(Collectors.groupingBy(ProductCategory::getParentId));

        List<ProductCategory> roots = all.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .collect(Collectors.toList());

        return buildTree(roots, childrenMap);
    }

    private List<ProductCategory> buildTree(List<ProductCategory> parents, Map<Long, List<ProductCategory>> childrenMap) {
        List<ProductCategory> result = new ArrayList<>();
        for (ProductCategory parent : parents) {
            ProductCategory node = new ProductCategory();
            node.setId(parent.getId());
            node.setName(parent.getName());
            node.setParentId(parent.getParentId());
            node.setLevel(parent.getLevel());
            node.setSort(parent.getSort());
            node.setIcon(parent.getIcon());
            node.setStatus(parent.getStatus());

            List<ProductCategory> children = childrenMap.get(parent.getId());
            if (children != null && !children.isEmpty()) {
                node.setChildren(buildTree(children, childrenMap));
            } else {
                node.setChildren(new ArrayList<>());
            }
            result.add(node);
        }
        return result;
    }
}