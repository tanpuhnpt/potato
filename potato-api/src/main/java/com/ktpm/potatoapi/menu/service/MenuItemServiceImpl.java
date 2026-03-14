package com.ktpm.potatoapi.menu.service;

import com.ktpm.potatoapi.category.entity.Category;
import com.ktpm.potatoapi.category.repo.CategoryRepository;
import com.ktpm.potatoapi.cloudinary.CloudinaryService;
import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.merchant.service.MerchantContextProvider;
import com.ktpm.potatoapi.menu.dto.MenuItemDetailResponse;
import com.ktpm.potatoapi.menu.dto.MenuItemRequest;
import com.ktpm.potatoapi.menu.dto.MenuItemResponse;
import com.ktpm.potatoapi.menu.entity.MenuItem;
import com.ktpm.potatoapi.menu.mapper.MenuItemMapper;
import com.ktpm.potatoapi.menu.repo.MenuItemRepository;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.merchant.repo.MerchantRepository;
import com.ktpm.potatoapi.option.repo.OptionRepository;
import com.ktpm.potatoapi.redis.RedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MenuItemServiceImpl implements MenuItemService {
    MenuItemMapper menuItemMapper;
    MenuItemRepository menuItemRepository;
    MerchantRepository merchantRepository;
    CategoryRepository categoryRepository;
    CloudinaryService cloudinaryService;
    MerchantContextProvider merchantContextProvider;
    OptionRepository optionRepository;
    RedisService redisService;

    @Override
    public List<MenuItemResponse> getAllMenuItemsOfMyMerchant() {
        Long merchantId = merchantContextProvider.getCurrentMerchant().getId();

        String key = String.format("menuitem:merchant:%d", merchantId);
        List<MenuItemResponse> responses = redisService.getAll(key, MenuItemResponse.class);

        if (responses == null) {
            log.info("query menu item");
            responses = menuItemRepository
                    .findAllByMerchantIdAndIsActiveTrue(merchantId)
                    .stream()
                    .map(menuItemMapper::toMenuItemResponse)
                    .toList();

            redisService.saveAll(key, responses);
        }

        return responses;
    }

    @Override
    public List<MenuItemResponse> getAllMenuItemsForCustomer(Long merchantId) {
        String key = String.format("menuitem:customer:merchant:%d", merchantId);
        List<MenuItemResponse> responses = redisService.getAll(key, MenuItemResponse.class);

        if (responses == null) {
            log.info("query menu item");
            Merchant merchant = merchantRepository.findById(merchantId)
                    .orElseThrow(() -> new AppException(ErrorCode.MERCHANT_NOT_FOUND));

            if (!merchant.isOpen())
                throw new AppException(ErrorCode.MERCHANT_CLOSED);

            responses = menuItemRepository
                    .findAllByMerchantIdAndIsVisibleTrue(merchantId)
                    .stream()
                    .map(menuItemMapper::toMenuItemResponse)
                    .toList();

            redisService.saveAll(key, responses);
        }

        return responses;
    }

    @Override
    public MenuItemDetailResponse getMenuItem(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findByIdAndIsActiveTrue(menuItemId)
                .orElseThrow(() -> new AppException(ErrorCode.MENU_ITEM_NOT_FOUND));
        log.info("Get menu item {}", menuItemId);

        return menuItemMapper.toMenuItemDetailResponse(menuItem);
    }

    @Override
    public MenuItemDetailResponse createMenuItem(MenuItemRequest menuItemRequest) {
        Category category = categoryRepository.findByIdAndIsActiveTrue(menuItemRequest.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Merchant merchant = merchantContextProvider.getCurrentMerchant();

        MenuItem menuItem = menuItemMapper.toEntity(menuItemRequest);
        menuItem.setImgUrl(uploadMenuItemImage(menuItemRequest.getImgFile()));
        menuItem.setCategory(category);
        menuItem.setMerchant(merchant);

        try {
            menuItemRepository.save(menuItem);
            log.info("{} created item {} in category {}", merchant.getName(), menuItem.getName(), category.getName());

            return menuItemMapper.toMenuItemDetailResponse(menuItem);
        } catch (DataIntegrityViolationException e) {
            log.error("Menu item with name {} already exists in {}", menuItem.getName(), merchant.getName());
            throw new AppException(ErrorCode.MENU_ITEM_EXISTED);
        }
    }

    @Override
    public MenuItemDetailResponse updateMenuItem(Long menuItemId, MenuItemRequest menuItemRequest) {
        MenuItem menuItem = menuItemRepository.findByIdAndIsActiveTrue(menuItemId)
                .orElseThrow(() -> new AppException(ErrorCode.MENU_ITEM_NOT_FOUND));

        Category category = categoryRepository.findByIdAndIsActiveTrue(menuItemRequest.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // Check menu item must be owned of current merchant
        Merchant merchant = merchantContextProvider.getCurrentMerchant();
        if(!Objects.equals(menuItem.getMerchant().getId(), merchant.getId()))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        menuItemMapper.updateMenuItem(menuItem, menuItemRequest);
        menuItem.setCategory(category);
        menuItem.setImgUrl(uploadMenuItemImage(menuItemRequest.getImgFile()));
        menuItemRepository.save(menuItem);

        log.info("{} updated item {} in category {}", merchant.getName(), menuItem.getName(), category.getName());

        return menuItemMapper.toMenuItemDetailResponse(menuItem);
    }

    @Override
    public MenuItemResponse updateMenuItemVisibleStatus(Long menuItemId, boolean isVisible) {
        MenuItem menuItem = menuItemRepository.findByIdAndIsActiveTrue(menuItemId)
                .orElseThrow(() -> new AppException(ErrorCode.MENU_ITEM_NOT_FOUND));

        // Check menu item must be owned of current merchant
        Merchant merchant = merchantContextProvider.getCurrentMerchant();
        if(!Objects.equals(menuItem.getMerchant().getId(), merchant.getId()))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        menuItem.setVisible(isVisible);
        menuItemRepository.save(menuItem);

        return menuItemMapper.toMenuItemResponse(menuItem);
    }

    @Override
    public void deleteMenuItem(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findByIdAndIsActiveTrue(menuItemId)
                .orElseThrow(() -> new AppException(ErrorCode.MENU_ITEM_NOT_FOUND));

        // Check menu item must be owned of current merchant
        Merchant merchant = merchantContextProvider.getCurrentMerchant();
        if(!Objects.equals(menuItem.getMerchant().getId(), merchant.getId()))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        // Remove the menu item from all associated options
        optionRepository.deleteAllOptionLinksByMenuItemId(menuItemId);

        menuItem.setVisible(false);
        menuItem.setActive(false);
        menuItemRepository.save(menuItem);

        log.info("Delete menu item {}", menuItem.getName());
    }

    private String uploadMenuItemImage(MultipartFile file) {
        return cloudinaryService.upload(file, "menu_items");
    }
}
