package com.ktpm.potatoapi.cart.service;

import com.ktpm.potatoapi.cart.dto.CartItemRequest;
import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.menu.entity.MenuItem;
import com.ktpm.potatoapi.menu.repo.MenuItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartServiceImpl implements CartService {
    MenuItemRepository menuItemRepository;

    @Override
    public boolean isSameMerchant(List<CartItemRequest> cartRequest, Long menuItemId) {
        MenuItem currentMenuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new AppException(ErrorCode.MENU_ITEM_NOT_FOUND));

        // Nếu giỏ hàng trống không có gì hết thì cho thêm vào giỏ hàng
        if(cartRequest.isEmpty())
            return true;

        // Lấy phần tử đầu tiên của danh sách các menu item trong cart để so sánh
        Long firstMenuItemId = cartRequest.get(0).getMenuItemId();
        MenuItem firstMenuItem = menuItemRepository.findById(firstMenuItemId)
                .orElseThrow(() -> new AppException(ErrorCode.MENU_ITEM_NOT_FOUND));

        // Kiểm tra có cùng một nhà hàng hay không
        return firstMenuItem.getMerchant().equals(currentMenuItem.getMerchant());
    }
}
