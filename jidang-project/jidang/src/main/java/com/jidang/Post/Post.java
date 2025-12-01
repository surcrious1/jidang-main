package com.jidang.Post;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import java.time.LocalDateTime;

import com.jidang.Comments.Comments;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import jakarta.persistence.ManyToOne;
import com.jidang.user.SiteUser;
import com.jidang.Game.Game;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import com.jidang.PostTag.PostTag;

@Getter
@Setter
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate; //작성일자

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comments> commentsList;

    ////고려사항 : 양방향이면(siteuser에도 manytoone으로 post를 가리키면) User 객체에서 작성한 게시글 리스트 조회 가능
    @ManyToOne //여러 post가 하나의 유저를 가리킬수있도록
    private SiteUser author; //작성자

    @ManyToOne
    @JoinColumn(name = "game_id") //game_id는 Game의 id랑 연결
    private Game game; //게임 종류

    // 저장된 파일의 이름 (예: uuid_image.jpg)
    private String filename;

    // 웹에서 접근할 때 사용할 경로 (예: /images/uuid_image.jpg)
    private String filepath;

    @ManyToMany
    @JoinTable( // 중간 테이블 설정
        name = "post_liker", // 테이블 이름
        joinColumns = @JoinColumn(name = "post_id"), // Post의 PK
        inverseJoinColumns = @JoinColumn(name = "site_user_id") // SiteUser의 PK
    )
    private Set<SiteUser> liker; // '좋아요'를 누른 사용자 목록

    //수정 시간
    private LocalDateTime modifyDate;


    //** Tag랑 연결해주기 위한것(Tag로 공략, 뉴비가이드 등 게시물 종류 구별)
    // PostTag와 1:N 관계 설정
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();

    /**
     * 편의 메서드: PostTag 엔티티를 통해 Tag를 추가
     * @param postTag Post와 Tag가 연결된 엔티티
     */
    public void addPostTag(PostTag postTag) {
        this.postTags.add(postTag);
        postTag.setPost(this); // 양방향 연결
    }
}

