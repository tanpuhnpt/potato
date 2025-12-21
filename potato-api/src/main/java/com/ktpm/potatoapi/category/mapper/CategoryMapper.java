package com.ktpm.potatoapi.category.mapper;

import com.ktpm.potatoapi.category.dto.CategoryResponse;
import com.ktpm.potatoapi.category.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category entity);
}
