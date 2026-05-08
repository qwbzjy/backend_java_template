package com.mall.api.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "分类响应")
public class CategoryVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "子分类")
    private List<CategoryVO> children;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}