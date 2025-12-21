package com.ktpm.potatoapi.menu.controller;

import com.ktpm.potatoapi.menu.dto.MenuItemRequest;
import com.ktpm.potatoapi.menu.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Menu Item Controller", description = "APIs for menu item")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin("*")
public class MenuItemController {
    MenuItemService menuItemService;

    @GetMapping(path = "/merchant/menu-items")
    @Operation(summary = "Show all menu items for Merchant Admin",
            description = "API for Merchant Admin to retrieve a list of all menu items")
    public ResponseEntity<?> getAllMenuItemsForMerAdmin() {
        return ResponseEntity.ok(menuItemService.getAllMenuItemsOfMyMerchant());
    }

    @GetMapping(path = {"/merchant/menu-items/{menuItemId}", "/menu-items/{menuItemId}"})
    @Operation(summary = "Show a menu item for Merchant Admin and Customer",
            description = "API for Merchant Admin and Customer to retrieve a specific menu item")
    public ResponseEntity<?> getMenuItem(@PathVariable Long menuItemId) {
        return ResponseEntity.ok(menuItemService.getMenuItem(menuItemId));
    }

    @PostMapping(path = "/merchant/menu-items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new menu item",
            description = "API for Merchant Admin to create a new menu item")
    public ResponseEntity<?> createMenuItem(@ModelAttribute @Valid MenuItemRequest request) {
        return ResponseEntity.ok(menuItemService.createMenuItem(request));
    }

    @PutMapping(path = "/merchant/menu-items/{menuItemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update menu item",
            description = "API for Merchant Admin to update menu item")
    public ResponseEntity<?> updateMenuItem(@PathVariable Long menuItemId,
                                            @ModelAttribute @Valid MenuItemRequest request) {
        return ResponseEntity.ok(menuItemService.updateMenuItem(menuItemId, request));
    }

    @PatchMapping(path = "/merchant/menu-items/{menuItemId}/isVisible")
    @Operation(summary = "Update menu item's visible status",
            description = "API for Merchant Admin to update menu item's visible status")
    public ResponseEntity<?> updateMenuItemVisibleStatus(@PathVariable Long menuItemId,
                                                         @RequestParam boolean isVisible) {
        return ResponseEntity.ok(menuItemService.updateMenuItemVisibleStatus(menuItemId, isVisible));
    }

    @DeleteMapping(path = "/merchant/menu-items/{menuItemId}")
    @Operation(summary = "Delete menu item",
            description = "API for Merchant Admin to delete menu item")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long menuItemId) {
        menuItemService.deleteMenuItem(menuItemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/merchants/{merchantId}/menu-items")
    @Operation(summary = "Show all menu items of a merchant for Customer",
            description = "API for Customer to retrieve a list of all menu items of a merchant")
    public ResponseEntity<?> getAllMenuItemsForCustomer(@PathVariable Long merchantId) {
        return ResponseEntity.ok(menuItemService.getAllMenuItemsForCustomer(merchantId));
    }
}
