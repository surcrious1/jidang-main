package com.jidang;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; //암호화하여 비밀번호 저장하는데 사용
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableWebSecurity //모든 요청 URL이 스프링 시큐리티 필터체인을 거치게 함(필터체인은 검문소같은거)
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean //스프링에 의해 생성 또는 관리되는 객체를 의미 ex)컨트롤러, 서비스, 리포지터리 등도 빈에 해당
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 모든 요청을 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                )
                // 개발 편의상 CSRF 비활성화 (필요시 제거 가능)
                //.csrf(csrf -> csrf.disable()) // 비활성화 문제로 주석처리
                //.formLogin 메서드는 스프링 시큐리티의 로그인 설정을 담당
                .formLogin((formLogin) -> formLogin
                    .loginPage("/user/login") //로그인 페이지의 URL은 /user/login
                    .defaultSuccessUrl("/")) //로그인 성공 시 이동할 페이지는 루트

                .logout(logout -> logout
                    .logoutUrl("/user/logout")
                    .logoutSuccessUrl("/") //로그아웃이 성공하면 어디로 이동할지 지정
                    .invalidateHttpSession(true) //로그아웃 시 현재 세션 초기화
                );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
