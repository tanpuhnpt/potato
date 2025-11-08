package com.ktpm.potatoapi.feedback.mapper;

import com.ktpm.potatoapi.feedback.dto.FeedbackResponse;
import com.ktpm.potatoapi.feedback.entity.Feedback;
import com.ktpm.potatoapi.user.mapper.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface FeedbackMapper {
    FeedbackResponse toResponse(Feedback entity);
}
