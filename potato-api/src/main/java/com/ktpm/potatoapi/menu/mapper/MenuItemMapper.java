package com.ktpm.potatoapi.menu.mapper;

import com.ktpm.potatoapi.category.mapper.CategoryMapper;
import com.ktpm.potatoapi.menu.dto.MenuItemDetailResponse;
import com.ktpm.potatoapi.menu.dto.MenuItemRequest;
import com.ktpm.potatoapi.menu.dto.MenuItemResponse;
import com.ktpm.potatoapi.menu.entity.MenuItem;
import com.ktpm.potatoapi.option.mapper.OptionMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, OptionMapper.class})
public interface MenuItemMapper {
    MenuItem toEntity(MenuItemRequest request);
    MenuItemResponse toMenuItemResponse(MenuItem menuItem);
    MenuItemDetailResponse toMenuItemDetailResponse(MenuItem menuItem);
    void updateMenuItem(@MappingTarget MenuItem menuItem, MenuItemRequest menuItemRequest);
}
