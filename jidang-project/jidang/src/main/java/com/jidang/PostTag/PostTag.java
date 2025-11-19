package com.jidang.PostTag;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

import com.jidang.Post.Post;
import com.jidang.Tag.Tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Post와 N:1 관계 설정 (FK: post_id)
    @ManyToOne(fetch = FetchType.LAZY) //FetchType는 데이터를 가져오는 법 설정-LAZY는 지연로딩
    @JoinColumn(name = "post_id") //엔티티 필드와 데이터베이스 테이블의 어떤 컬럼을 연결(Join)할지를 명시
    private Post post;

    // Tag와 N:1 관계 설정 (FK: tag_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    // 기본 생성자
    public PostTag() {}

    // PostTag 생성 메서드-서비스에서 사용예정
    public static PostTag createPostTag(Tag tag) {
        PostTag postTag = new PostTag();

        // 1. Tag 객체를 설정합니다. (Tag는 PostTag의 ManyToOne 관계이므로 관계의 주인)
        postTag.setTag(tag);

        // 2. Post 객체는 Post.addPostTag() 메서드에서 설정됩니다.
        // postTag.setPost(post);

        return postTag;
    }

}
