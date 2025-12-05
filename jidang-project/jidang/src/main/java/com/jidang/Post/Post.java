package com.jidang.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jidang.Comments.Comments;
import com.jidang.Game.Game;
import com.jidang.PostTag.PostTag;
import com.jidang.user.SiteUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Post {

    /* =========================
       기본 필드
    ========================= */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    /* =========================
       작성자 / 게임
    ========================= */
    @ManyToOne
    private SiteUser author;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    /* =========================
       댓글
    ========================= */
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comments> commentsList = new ArrayList<>();

    /* =========================
       익명 여부
    ========================= */
    // true 이면 닉네임 대신 "익명" 등으로 처리
    private boolean anonymous;

    /* =========================
       좋아요 (다대다)
    ========================= */
    @ManyToMany
    @JoinTable(
            name = "post_liker",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "site_user_id")
    )
    private Set<SiteUser> liker;

    /* =========================
       태그 (PostTag 중간 엔티티)
    ========================= */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();

    public void addPostTag(PostTag postTag) {
        this.postTags.add(postTag);
        postTag.setPost(this);   // PostTag 쪽에 setPost(...) 가 있어야 함
    }

    /* =========================
       여러 이미지
    ========================= */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    public void addImage(PostImage image) {
        this.images.add(image);
        // PostImage 쪽에 setPostRelation(...) 대신 setPost(...) 라면 아래를 바꿔줘
        image.setPostRelation(this);
    }
}
