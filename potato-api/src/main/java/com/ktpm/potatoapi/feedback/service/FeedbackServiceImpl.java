package com.ktpm.potatoapi.feedback.service;

import com.ktpm.potatoapi.cloudinary.CloudinaryService;
import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.merchant.service.MerchantContextProvider;
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
import com.ktpm.potatoapi.redis.RedisService;
import com.ktpm.potatoapi.security.AuthContextProvider;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {
    FeedbackRepository feedbackRepository;
    UserRepository userRepository;
    OrderRepository orderRepository;
    MerchantRepository merchantRepository;
    AuthContextProvider authContextProvider;
    MerchantContextProvider merchantContextProvider;
    CloudinaryService cloudinaryService;
    FeedbackMapper mapper;
    RedisService redisService;

    @Override
    @Transactional
    public void giveFeedback(GiveFeedbackRequest request) {
        User customer = userRepository.findByEmail(authContextProvider.getCurrentUserEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Check xem order đã ở trạng thái COMPLETED chưa
        if (order.getStatus() != OrderStatus.COMPLETED)
            throw new AppException(ErrorCode.ORDER_NOT_COMPLETED);

        // Check lại xem order này có phải thuộc về customer này không
        if (!Objects.equals(order.getCustomer().getId(), customer.getId()))
            throw new AppException(ErrorCode.ORDER_NOT_OWNED_BY_CURRENT_USER);

        Merchant merchant = order.getMerchant();
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
    public List<FeedbackResponse> getAllFeedbacksForCustomer(Long merchantId) {
        String key = String.format("feedback:merchant:%d", merchantId);
        List<FeedbackResponse> responses = redisService.getAll(key, FeedbackResponse.class);

        if (responses == null) {
            log.info("query feedback");
            Merchant merchant = merchantRepository.findById(merchantId)
                    .orElseThrow(() -> new AppException(ErrorCode.MERCHANT_NOT_FOUND));

            if (!merchant.isOpen())
                throw new AppException(ErrorCode.MERCHANT_CLOSED);

            responses = feedbackRepository.findAllByMerchantId(merchantId)
                    .stream()
                    .map(mapper::toResponse)
                    .toList();

            redisService.saveAll(key, responses);
        }

        return responses;
    }

    @Override
    public FeedbackResponse replyFeedback(Long id, ReplyFeedbackRequest request) {
        Feedback customerFeedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));
        User merchantAdmin = userRepository.findByEmail(authContextProvider.getCurrentUserEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // verify merchant ownership
        if (!Objects.equals(customerFeedback.getMerchant().getMerchantAdmin().getId(), merchantAdmin.getId()))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        Feedback feedback = buildFeedback(merchantAdmin, customerFeedback.getOrder(), customerFeedback.getMerchant());
        feedback.setComment(request.getComment());
        feedbackRepository.save(feedback);

        return mapper.toResponse(feedback);
    }

    @Override
    public List<FeedbackResponse> getAllFeedbacksOfMyMerchant() {
        Long merchantId = merchantContextProvider.getCurrentMerchant().getId();

        String key = String.format("feedback:merchant:%d", merchantId);
        List<FeedbackResponse> responses = redisService.getAll(key, FeedbackResponse.class);

        if (responses == null) {
            log.info("query feedback");

            responses = feedbackRepository.findAllByMerchantId(merchantId)
                    .stream()
                    .map(mapper::toResponse)
                    .toList();

            redisService.saveAll(key, responses);
        }

        return responses;
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
