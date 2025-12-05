package com.jidang.user;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)//유일값만 넣을 수 있음
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    //가입일
    @Column(name = "join_date", nullable = false, updatable = false)
    private LocalDateTime joinDate;

    // 칭호 목록 (중복 방지를 위해 Set 사용)
    // @ElementCollection은 별도의 엔티티 없이 값 타입 컬렉션을 저장할 때 사용합니다.
    // DB에는 site_user_titles 라는 별도 테이블이 자동 생성됩니다.
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> titles = new HashSet<>();
    
    // 칭호 추가 편의 메서드
    public void addTitle(String title) {
        this.titles.add(title);
    }
}
