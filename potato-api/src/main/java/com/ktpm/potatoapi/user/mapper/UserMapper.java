package com.ktpm.potatoapi.user.mapper;

import com.ktpm.potatoapi.user.dto.SignUpRequest;
import com.ktpm.potatoapi.user.dto.UserInFeedbackResponse;
import com.ktpm.potatoapi.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toEntity(SignUpRequest signUpRequest);

    UserInFeedbackResponse toUserInFeedbackResponse(User entity);
}
