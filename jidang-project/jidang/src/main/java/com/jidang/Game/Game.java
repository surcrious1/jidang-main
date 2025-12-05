package com.jidang.Game;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import com.jidang.Post.Post;

@Getter
@Setter
@Entity
public class Game {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;       // 게임의 실제이름(사람이 보는)

    @Column(nullable = false, unique = true, length = 50)
    private String slug;       // 게임의 정식명칭외의 url에서 표시하기 위한 명칭(띄어쓰기 있을 수 있어서)

    @Column(length = 255)
    private String thumbnail;  // 게임을 대표하는 썸네일 이미지 경로

    @OneToMany(mappedBy = "game")
    private List<Post> posts; // Game과 Post 연결

    // 게임 설명 작성
    @Column(length = 500)
    private String description;

}
