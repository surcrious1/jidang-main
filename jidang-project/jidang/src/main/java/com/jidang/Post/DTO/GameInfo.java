package com.jidang.Post.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameInfo {
    private String name;     // 게임 이름
    private String image;    // 게임 이미지 경로
    private String desc;     // 게임 설명
}
