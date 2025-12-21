package com.ktpm.potatoapi.option.mapper;

import com.ktpm.potatoapi.option.dto.OptionValueRequest;
import com.ktpm.potatoapi.option.dto.OptionValueResponse;
import com.ktpm.potatoapi.option.entity.OptionValue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OptionValueMapper {
    OptionValue toEntity(OptionValueRequest dto);
    OptionValueResponse toResponse(OptionValue entity);
}
