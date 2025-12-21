package com.ktpm.potatoapi.option.service;

import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.common.utils.SecurityUtils;
import com.ktpm.potatoapi.menu.entity.MenuItem;
import com.ktpm.potatoapi.menu.repo.MenuItemRepository;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.option.dto.*;
import com.ktpm.potatoapi.option.entity.Option;
import com.ktpm.potatoapi.option.entity.OptionValue;
import com.ktpm.potatoapi.option.mapper.OptionMapper;
import com.ktpm.potatoapi.option.mapper.OptionValueMapper;
import com.ktpm.potatoapi.option.repo.OptionRepository;
import com.ktpm.potatoapi.option.repo.OptionValueRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OptionServiceImpl implements OptionService {
    OptionRepository optionRepository;
    OptionMapper optionMapper;
    OptionValueMapper optionValueMapper;
    SecurityUtils securityUtils;
    OptionValueRepository optionValueRepository;
    MenuItemRepository menuItemRepository;

    @Override
    public List<OptionResponse> getAllOptionsOfMyMerchant() {
        List<Option> options = optionRepository.findAllByMerchantIdAndIsActiveTrue(
                securityUtils.getCurrentMerchant().getId());
        log.info("Get all options for Merchant Admin");

        return options
                .stream()
                .map(optionMapper::toOptionResponse)
                .toList();
    }

    @Override
    public OptionDetailResponse getOptionForMerAdmin(Long optionId) {
        Option option = optionRepository.findByIdAndIsActiveTrue(optionId)
                .orElseThrow(() -> new AppException(ErrorCode.OPTION_NOT_FOUND));

        // validate merchant ownership
        Merchant merchantOfOption = option.getMerchant();
        if (!merchantOfOption.equals(securityUtils.getCurrentMerchant()))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        return optionMapper.toOptionDetailResponse(option);
    }

    @Override
    @Transactional
    public OptionResponse createOptionAndOptionValue(OptionCreationRequest request) {
        Merchant merchant = securityUtils.getCurrentMerchant();

        // map request to entity
        Option option = Option.builder()
                .name(request.getName())
                .isRequired(request.isRequired())
                .merchant(merchant)
                .build();

        List<OptionValue> optionValues = request.getOptionValues().stream()
                .map(optionValueRequest -> {
                    OptionValue entity = optionValueMapper.toEntity(optionValueRequest);
                    entity.setOption(option);
                    entity.setDefault(false);
                    return entity;
                })
                .toList();

        // nếu option có isRequired=true thì value[0] phải isDefault=true
        if (request.isRequired()) optionValues.get(0).setDefault(true);
        option.setOptionValues(optionValues);

        try {
            optionRepository.save(option);
            log.info("Create option '{}' for merchant {} with {} value(s)",
                    option.getName(), merchant.getName(), optionValues.size());

            return optionMapper.toOptionResponse(option);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.OPTION_EXISTED);
        }
    }

    @Override
    public OptionResponse createOptionValueForExistingOption(Long optionId, OptionValueRequest request) {
        Option option = optionRepository.findByIdAndIsActiveTrue(optionId)
                .orElseThrow(() -> new AppException(ErrorCode.OPTION_NOT_FOUND));

        // validate merchant ownership
        Merchant merchantOfOption = option.getMerchant();
        if (!merchantOfOption.equals(securityUtils.getCurrentMerchant()))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        OptionValue optionValue = optionValueMapper.toEntity(request);
        optionValue.setOption(option);
        optionValue.setDefault(false);

        if (!option.isVisible()) {
            option.setVisible(true);

            if (option.isRequired()) optionValue.setDefault(true);
        }

        try {
            optionValueRepository.save(optionValue);
            log.info("Create option value {} for option {}", optionValue.getName(), option.getName());

            return optionMapper.toOptionResponse(option);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.OPTION_VALUE_EXISTED);
        }
    }

    @Override
    public OptionResponse updateOption(Long optionId, OptionUpdateRequest request) {
        Option option = optionRepository.findByIdAndIsActiveTrue(optionId)
                .orElseThrow(() -> new AppException(ErrorCode.OPTION_NOT_FOUND));

        // validate merchant ownership
        Merchant merchantOfOption = option.getMerchant();
        if (!merchantOfOption.equals(securityUtils.getCurrentMerchant()))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        option.setName(request.getName());
        try {
            optionRepository.save(option);
            log.info("Update name of option {}", option.getName());

            return optionMapper.toOptionResponse(option);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.OPTION_EXISTED);
        }
    }

    @Override
    public OptionValueResponse updateOptionValue(Long valueId, OptionValueRequest request) {
        OptionValue optionValue = optionValueRepository.findByIdAndIsActiveTrue(valueId)
                .orElseThrow(() -> new AppException(ErrorCode.OPTION_VALUE_NOT_FOUND));

        Option optionOfOptionValue = optionRepository.findByIdAndIsActiveTrue(optionValue.getOption().getId())
                .orElseThrow(() -> new AppException(ErrorCode.OPTION_NOT_FOUND));

        // validate merchant ownership
        Merchant merchantOfOption = optionOfOptionValue.getMerchant();
        if (!merchantOfOption.equals(securityUtils.getCurrentMerchant()))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        optionValue.setName(request.getName());
        optionValue.setExtraPrice(request.getExtraPrice());

        try {
            optionValueRepository.save(optionValue);
            log.info("Updated option value {}", optionValue.getName());

            return optionValueMapper.toResponse(optionValue);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.OPTION_VALUE_EXISTED);
        }
    }

    @Override
    public OptionValueResponse updateOptionValueVisibleStatus(Long valueId, boolean isVisible) {
        OptionValue optionValue = optionValueRepository.findByIdAndIsActiveTrue(valueId)
                .orElseThrow(() -> new AppException(ErrorCode.OPTION_VALUE_NOT_FOUND));

        Option optionOfOptionValue = optionRepository.findByIdAndIsActiveTrue(optionValue.getOption().getId())
                .orElseThrow(() -> new AppException(ErrorCode.OPTION_NOT_FOUND));

        // validate merchant ownership
        Merchant merchantOfOption = optionOfOptionValue.getMerchant();
        if (!merchantOfOption.equals(securityUtils.getCurrentMerchant()))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        optionValue.setVisible(isVisible);

        List<OptionValue> allValues = optionOfOptionValue.getOptionValues();
        List<OptionValue> visibleValues = allValues.stream()
                .filter(OptionValue::isVisible)
                .toList();

        if (!isVisible && optionValue.isDefault()) {
            // bỏ default option value đang muốn xóa hiện tại
            optionValue.setDefault(false);

            // chọn cái visible đầu tiên trong visibleValues làm default mới
            if (!visibleValues.isEmpty())
                visibleValues.get(0).setDefault(true);
        }

        optionOfOptionValue.setVisible(!visibleValues.isEmpty());

        optionValueRepository.saveAll(allValues);
        optionRepository.save(optionOfOptionValue);

        log.info("Update {}'s visible status", optionValue.getName());

        return optionValueMapper.toResponse(optionValue);
    }

    @Override
    public OptionDetailResponse assignMenuItemToOption(Long optionId, AddMenuItemToOptionRequest request) {
        Option option = optionRepository.findByIdAndIsActiveTrue(optionId)
                .orElseThrow(() -> new AppException(ErrorCode.OPTION_NOT_FOUND));

        // validate merchant ownership
        Merchant merchantOfOption = option.getMerchant();
        if (!merchantOfOption.equals(securityUtils.getCurrentMerchant()))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        List<MenuItem> menuItems =  menuItemRepository.findAllByIdInAndMerchantIdAndIsActiveTrue(
                request.getMenuItemIds(), merchantOfOption.getId());

        if (request.getMenuItemIds().size() != menuItems.size())
            throw new AppException(ErrorCode.MENU_ITEM_NOT_FOUND);

        option.setMenuItems(new ArrayList<>(menuItems));
        optionRepository.save(option);

        log.info("Assign menu item(s) to option: {}", option.getName());

        return optionMapper.toOptionDetailResponse(option);
    }

    @Override
    public void deleteOptionValue(Long valueId) {
        OptionValue optionValue = optionValueRepository.findByIdAndIsActiveTrue(valueId)
                .orElseThrow(() -> new AppException(ErrorCode.OPTION_VALUE_NOT_FOUND));

        Option optionOfOptionValue = optionRepository.findByIdAndIsActiveTrue(optionValue.getOption().getId())
                .orElseThrow(() -> new AppException(ErrorCode.OPTION_NOT_FOUND));

        // validate merchant ownership
        Merchant merchantOfOption = optionOfOptionValue.getMerchant();
        if (!merchantOfOption.equals(securityUtils.getCurrentMerchant()))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        optionValue.setActive(false);
        optionValue.setVisible(false);

        List<OptionValue> allValues = optionOfOptionValue.getOptionValues();
        List<OptionValue> visibleValues = allValues.stream()
                .filter(OptionValue::isVisible)
                .toList();

        // xử lí nếu optionValue là default
        if (optionValue.isDefault()) {
            optionValue.setDefault(false);

            // chọn cái visible đầu tiên trong visibleValues làm default mới
            if (!visibleValues.isEmpty())
                visibleValues.get(0).setDefault(true);
        }

        // xử lí nếu đó là optionValue duy nhất của option
        optionOfOptionValue.setVisible(!visibleValues.isEmpty());

        optionValueRepository.saveAll(allValues);
        optionRepository.save(optionOfOptionValue);

        log.info("Delete option value {}", optionValue.getName());
    }

    @Override
    public void deleteOption(Long optionId) {
        Option option = optionRepository.findByIdAndIsActiveTrue(optionId)
                .orElseThrow(() -> new AppException(ErrorCode.OPTION_NOT_FOUND));

        // validate merchant ownership
        Merchant merchantOfOption = option.getMerchant();
        if (!merchantOfOption.equals(securityUtils.getCurrentMerchant()))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        option.setActive(false);
        option.setVisible(false);

        // deactivate các value của option này
        List<OptionValue> optionValues = option.getOptionValues();
        for (OptionValue optionValue : optionValues) {
            optionValue.setActive(false);
            optionValue.setVisible(false);
        }

        // xóa liên kết với các menu item của option này
        option.getMenuItems().clear();

        optionRepository.save(option);

        log.info("Delete option {}", option.getName());
    }
}
