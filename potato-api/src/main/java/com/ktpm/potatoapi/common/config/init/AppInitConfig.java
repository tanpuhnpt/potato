package com.ktpm.potatoapi.common.config.init;

import com.ktpm.potatoapi.user.entity.Role;
import com.ktpm.potatoapi.user.entity.User;
import com.ktpm.potatoapi.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AppInitConfig {
    private final PasswordEncoder passwordEncoder;

    @NonFinal
    static final String adminMail = "tanpuhnpt@gmail.com";

    @NonFinal
    static final String adminPsw = "admin";

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail(adminMail).isEmpty()) {
                User sysAdmin = User.builder()
                        .email(adminMail)
                        .password(passwordEncoder.encode(adminPsw))
                        .role(Role.SYSTEM_ADMIN)
                        .build();
                userRepository.save(sysAdmin);
            }
        };
    }
}
