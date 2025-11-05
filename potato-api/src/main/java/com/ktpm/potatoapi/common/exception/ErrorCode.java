package com.ktpm.potatoapi.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // GLOBAL ERROR
    UNCATEGORIZED(9999, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    JSON_INVALID(1001, "Invalid JSON request", HttpStatus.BAD_REQUEST),
    MESSAGE_KEY_INVALID(1002, "Invalid message key", HttpStatus.BAD_REQUEST),
    BIND_INVALID(1003, "Binding error occurred", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1004, "Authentication is required", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "You do not have permission to access", HttpStatus.FORBIDDEN),
    MUST_BE_OWNED_OF_CURRENT_MERCHANT(1006,
            "Merchant Admin does not own the current merchant", HttpStatus.BAD_REQUEST),

    // AUTH ERROR
    EMAIL_BLANK(2001, "Email is required", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(2002, "Email is not well-formed", HttpStatus.BAD_REQUEST),
    PASSWORD_BLANK(2003, "Password is required", HttpStatus.BAD_REQUEST),
    PASSWORD_PATTERN_INVALID(2004,
            "Password must be 8-20 characters long and contain at least one uppercase, one lowercase, one digit, and one special character",
            HttpStatus.BAD_REQUEST),
    FULL_NAME_BLANK(2005, "Full name is required", HttpStatus.BAD_REQUEST),
    SAME_PASSWORD(2006, "New password must be different from current password", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(2007, "Invalid credentials", HttpStatus.BAD_REQUEST),

    // USER ERROR,
    USER_EXISTED(3001, "User already existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(3002, "User not existed", HttpStatus.NOT_FOUND),

    // MERCHANT ERROR,
    MERCHANT_EXISTED(4001, "Merchant already existed", HttpStatus.BAD_REQUEST),
    MERCHANT_NOT_FOUND(4002, "Merchant not existed", HttpStatus.NOT_FOUND),
    REGISTERED_MERCHANT_EXISTED(4003, "Registered merchant already existed", HttpStatus.BAD_REQUEST),
    REGISTERED_MERCHANT_NOT_FOUND(4004, "Registered merchant not existed", HttpStatus.NOT_FOUND),
    REGISTERED_MERCHANT_STATUS_NOT_PENDING(4013,
            "Merchant registration is not pending", HttpStatus.BAD_REQUEST),
    REGISTERED_MERCHANT_STATUS_NOT_CONFIRMED(4005,
            "Merchant registration is not confirmed", HttpStatus.BAD_REQUEST),
    REGISTERED_MERCHANT_ADMIN_FULL_NAME_BLANK(4006,
            "Registered merchant admin full name is required", HttpStatus.BAD_REQUEST),
    REGISTERED_MERCHANT_NAME_BLANK(4007, "Registered merchant name is required", HttpStatus.BAD_REQUEST),
    ADDRESS_BLANK(4008, "Merchant address is required", HttpStatus.BAD_REQUEST),
    OPENING_HOURS_EMPTY(4009, "Merchant opening hours are required", HttpStatus.BAD_REQUEST),
    CUISINE_TYPES_EMPTY(4010, "Merchant cuisine types are required", HttpStatus.BAD_REQUEST),
    INTRO_BLANK(4011, "Merchant introduction is required", HttpStatus.BAD_REQUEST),
    MERCHANT_CLOSED(4012, "This merchant is closed now", HttpStatus.BAD_REQUEST),

    // CUISINE TYPE ERROR,
    CUISINE_TYPE_EXISTED(5001, "Cuisine type already existed", HttpStatus.BAD_REQUEST),
    CUISINE_TYPE_NOT_FOUND(5002, "Cuisine type not existed", HttpStatus.NOT_FOUND),
    CUISINE_TYPE_NAME_BLANK(5003, "Cuisine type name is required", HttpStatus.BAD_REQUEST),
    CUISINE_TYPE_IMG_NULL(5004, "Cuisine type image file is required", HttpStatus.BAD_REQUEST),

    // CATEGORY ERROR
    CATEGORY_EXISTED(6001, "Category already existed in this merchant", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(6002, "Category not existed in this merchant", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_BLANK(6003, "Category name is required", HttpStatus.BAD_REQUEST),

    // OPTION ERROR
    OPTION_EXISTED(7001, "Option already existed in this merchant", HttpStatus.BAD_REQUEST),
    OPTION_NOT_FOUND(7002, "Option not existed in this merchant", HttpStatus.NOT_FOUND),
    OPTION_NAME_BLANK(7003, "Option name is required", HttpStatus.BAD_REQUEST),
    OPTION_REQUIRED_STATUS_NULL(7004, "Option required status is required", HttpStatus.BAD_REQUEST),
    OPTION_VALUES_EMPTY(7005, "Option value set is required", HttpStatus.BAD_REQUEST),
    OPTION_VALUE_EXISTED(7006, "Option value already existed", HttpStatus.BAD_REQUEST),
    OPTION_VALUE_INVISIBLE(7007, "Option value is invisible", HttpStatus.BAD_REQUEST),
    OPTION_VALUE_NOT_FOUND(7008, "Option value not existed", HttpStatus.NOT_FOUND),
    OPTION_VALUE_NAME_BLANK(7009, "Option value name is required", HttpStatus.BAD_REQUEST),
    OPTION_VALUE_EXTRA_PRICE_NEGATIVE(
            7010,
            "Option value's extra price must be greater than or equal to 0",
            HttpStatus.BAD_REQUEST
    ),

    // MENU ITEM ERROR
    MENU_ITEM_EXISTED(8001, "Menu item already existed in this merchant", HttpStatus.BAD_REQUEST),
    MENU_ITEM_NOT_FOUND(8002, "Menu item not existed in this merchant", HttpStatus.NOT_FOUND),
    MENU_ITEM_NAME_BLANK(8003, "Menu item name is required", HttpStatus.BAD_REQUEST),
    MENU_ITEM_PRICE_NULL(8004, "Menu item base price is required", HttpStatus.BAD_REQUEST),
    MENU_ITEM_PRICE_NEG_OR_ZERO(
            8005,
            "Menu item's base price must be greater than or equal to 0",
            HttpStatus.BAD_REQUEST
    ),
    MENU_ITEM_FILE_NULL(8006, "Menu item file is required", HttpStatus.BAD_REQUEST),
    MENU_ITEM_CATEGORY_NULL(8007, "Menu item's category is required", HttpStatus.BAD_REQUEST),
    MENU_ITEM_INVISIBLE(8008, "Menu item is invisible", HttpStatus.BAD_REQUEST),
    MENU_ITEM_NOT_ASSIGNED_TO_OPTION(8009, "Menu item is not assigned to this option", HttpStatus.BAD_REQUEST),

    // ORDER ERROR
    ORDER_FULL_NAME_BLANK(9001, "Customer's full name is required", HttpStatus.BAD_REQUEST),
    ORDER_PHONE_BLANK(9002, "Customer's phone number is required", HttpStatus.BAD_REQUEST),
    ORDER_DELIVERY_ADDRESS(9003, "Customer's delivery address is required", HttpStatus.BAD_REQUEST),
    ORDER_CART_ITEMS_EMPTY(9004, "List of cart items is required", HttpStatus.BAD_REQUEST),
    ORDER_HAS_MULTIPLE_OPTION_VALUES_FOR_REQUIRED_OPTION(
            9005,
            "Order has more than 1 option values for a required option",
            HttpStatus.BAD_REQUEST
    ),
    ORDER_NOT_FOUND(9006, "Order not existed", HttpStatus.NOT_FOUND),
    ORDER_STATUS_INVALID_FOR_UPDATE(9007, "Cannot update a canceled order", HttpStatus.BAD_REQUEST),
    ORDER_STATUS_REQUEST_INVALID(9008, "Invalid order status in request", HttpStatus.BAD_REQUEST),
    ORDER_STATUS_INVALID_FOR_CANCEL(
            9009,
            "The order can only be canceled when status is CONFIRMED",
            HttpStatus.BAD_REQUEST
    ),
    ORDER_STATUS_NOT_STEP_BY_STEP(9010, "Order status must be step by step", HttpStatus.BAD_REQUEST),
    CANCEL_REASON_EMPTY(9011, "A reason is required when canceling an order", HttpStatus.BAD_REQUEST),
    ORDER_NOT_COMPLETED(9012, "The order has not been completed to rate", HttpStatus.BAD_REQUEST),
    ORDER_NOT_OWNED_BY_CURRENT_USER(9013, "The order is not owned by this user", HttpStatus.BAD_REQUEST),
    ORDER_RATED(9014, "This order is already rated", HttpStatus.BAD_REQUEST),

    // RATING ERROR,
    RATING_ORDER_NULL(10001, "Order is required for rating", HttpStatus.BAD_REQUEST),
    RATING_NULL(10002, "Rating value is required", HttpStatus.BAD_REQUEST),
    RATING_OUT_OF_RANGE(10003, "Rating value must be in the range of {min} to {max}", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
