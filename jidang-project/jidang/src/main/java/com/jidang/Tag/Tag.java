package com.jidang.Tag;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import com.jidang.PostTag.PostTag;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // 예: "공략", "뉴비가이드"

    // PostTag와 1:N 관계 설정 (읽기 전용)
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();

    // 기본 생성자
    public Tag() {}

    // 이름 생성자
    public Tag(String name) {
        this.name = name;
    }

}
