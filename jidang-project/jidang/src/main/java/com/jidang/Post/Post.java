package com.jidang.Post;

import java.util.List;

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

import lombok.Getter;
import lombok.Setter;

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
}
