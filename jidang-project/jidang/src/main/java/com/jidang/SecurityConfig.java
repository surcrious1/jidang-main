package com.jidang;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; //암호화하여 비밀번호 저장하는데 사용
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity //모든 요청 URL이 스프링 시큐리티 필터체인을 거치게 함(필터체인은 검문소같은거)
public class SecurityConfig {

    @Bean //스프링에 의해 생성 또는 관리되는 객체를 의미 ex)컨트롤러, 서비스, 리포지터리 등도 빈에 해당
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 모든 요청을 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                )
                // 개발 편의상 CSRF 비활성화 (필요시 제거 가능)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
