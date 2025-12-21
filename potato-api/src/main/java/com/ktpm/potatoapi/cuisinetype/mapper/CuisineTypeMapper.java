package com.ktpm.potatoapi.cuisinetype.mapper;

import com.ktpm.potatoapi.cuisinetype.dto.CuisineTypeResponse;
import com.ktpm.potatoapi.cuisinetype.entity.CuisineType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CuisineTypeMapper {
    CuisineTypeResponse toResponse(CuisineType entity);
}
