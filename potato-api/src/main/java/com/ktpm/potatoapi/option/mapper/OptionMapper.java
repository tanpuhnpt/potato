package com.ktpm.potatoapi.option.mapper;

import com.ktpm.potatoapi.option.dto.OptionDetailResponse;
import com.ktpm.potatoapi.option.dto.OptionResponse;
import com.ktpm.potatoapi.option.entity.Option;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = OptionValueMapper.class)
public interface OptionMapper {
    OptionResponse toOptionResponse(Option entity);
    OptionDetailResponse toOptionDetailResponse(Option entity);
}
