package com.jidang.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
    //유저 id로 siteuser 엔티티를 조회하는 메서드
    Optional<SiteUser> findByusername(String username);
}
