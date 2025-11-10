package com.ktpm.potatoapi.feedback.service;

import com.ktpm.potatoapi.cloudinary.CloudinaryService;
import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.common.utils.SecurityUtils;
import com.ktpm.potatoapi.feedback.dto.FeedbackResponse;
import com.ktpm.potatoapi.feedback.dto.ReplyFeedbackRequest;
import com.ktpm.potatoapi.feedback.mapper.FeedbackMapper;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.merchant.repo.MerchantRepository;
import com.ktpm.potatoapi.order.entity.Order;
import com.ktpm.potatoapi.order.entity.OrderStatus;
import com.ktpm.potatoapi.order.repo.OrderRepository;
import com.ktpm.potatoapi.feedback.dto.GiveFeedbackRequest;
import com.ktpm.potatoapi.feedback.entity.Feedback;
import com.ktpm.potatoapi.feedback.repo.FeedbackRepository;
import com.ktpm.potatoapi.user.entity.User;
import com.ktpm.potatoapi.user.repo.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {
    FeedbackRepository feedbackRepository;
    UserRepository userRepository;
    OrderRepository orderRepository;
    MerchantRepository merchantRepository;
    SecurityUtils securityUtils;
    CloudinaryService cloudinaryService;
    FeedbackMapper mapper;

    @Override
    @Transactional
    public void giveFeedback(GiveFeedbackRequest request) {
        User customer = userRepository.findByEmail(securityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        Merchant merchant = order.getMerchant();

        // Check xem order đã ở trạng thái COMPLETED chưa
        if (order.getStatus() != OrderStatus.COMPLETED)
            throw new AppException(ErrorCode.ORDER_NOT_COMPLETED);

        // Check lại xem order này có phải thuộc về customer này không
        if (!orderRepository.existsByIdAndCustomerId(order.getId(), customer.getId()))
            throw new AppException(ErrorCode.ORDER_NOT_OWNED_BY_CURRENT_USER);

        Feedback feedback = buildFeedback(customer, order, merchant);
        feedback.setComment(request.getComment());
        feedback.setRating(request.getRating());
        if (request.getImgFiles() != null && !request.getImgFiles().isEmpty()) {
            feedback.setImgUrl(uploadReviewImage(request.getImgFiles()));
        }

        try {
            feedbackRepository.save(feedback);
            log.info("Rating with order code {} is created", order.getCode());
        } catch (DataIntegrityViolationException e) {
            log.error("This order is already rated");
            throw new AppException(ErrorCode.ORDER_RATED);
        }

        merchant.setRatingCount(merchant.getRatingCount() + 1); // update rating count
        merchant.setAvgRating(feedbackRepository.calcAvgRatingByMerchant(merchant)); // calc avg rating
        merchantRepository.save(merchant);
    }

    @Override
    public FeedbackResponse replyFeedback(Long id, ReplyFeedbackRequest request) {
        Feedback customerFeedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));
        User merchantAdmin = userRepository.findByEmail(securityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Feedback feedback = buildFeedback(merchantAdmin, customerFeedback.getOrder(), customerFeedback.getMerchant());
        feedback.setComment(request.getComment());
        feedbackRepository.save(feedback);

        return mapper.toResponse(feedback);
    }

    private List<String> uploadReviewImage(List<MultipartFile> imgFiles) {
        List<String> imgUrls = new ArrayList<>();
        for (MultipartFile file : imgFiles)
            imgUrls.add(cloudinaryService.upload(file, "review"));

        return imgUrls;
    }

    private Feedback buildFeedback(User customer, Order order, Merchant merchant) {
        return Feedback.builder()
                .user(customer)
                .order(order)
                .merchant(merchant)
                .build();
    }
}
