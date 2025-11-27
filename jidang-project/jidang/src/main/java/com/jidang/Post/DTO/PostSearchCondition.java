package com.jidang.Post.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostSearchCondition {
    // 제목 또는 내용에 포함될 키워드
    private String keyword;

    // 게임 종류 이름 (Game 엔티티의 name으로 가정)
    private String gameType;

    // 태그 이름 (하나의 태그로 검색한다고 가정)
    private List<String> tags;
}
