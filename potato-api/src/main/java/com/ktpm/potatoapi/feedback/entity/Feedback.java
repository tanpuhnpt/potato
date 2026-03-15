package com.ktpm.potatoapi.feedback.entity;

import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.order.entity.Order;
import com.ktpm.potatoapi.redis.RedisListener;
import com.ktpm.potatoapi.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(RedisListener.class)
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    Merchant merchant;

    @ManyToOne
    User user;

    @ManyToOne
    Order order;

    Integer rating;
    LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    String comment;

    @ElementCollection
    List<String> imgUrl;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
