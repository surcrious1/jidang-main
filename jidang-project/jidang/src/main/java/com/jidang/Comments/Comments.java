package com.jidang.Comments;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Set;
import com.jidang.Post.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.CascadeType;
import lombok.Getter;
import lombok.Setter;

import com.jidang.user.SiteUser;

@Getter
@Setter
@Entity
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private Post post;

    @ManyToOne
    private SiteUser author; //작성자

    //수정 시간
    private LocalDateTime modifyDate;

    @ManyToOne // 부모 댓글 (대댓글이 아닌 경우 null)
    @JoinColumn(name = "parent_id")
    private Comments parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE) // 자식 댓글 목록
    private List<Comments> replies;

    @ManyToMany
    @JoinTable(
        name = "comments_liker",
        joinColumns = @JoinColumn(name = "comments_id"),
        inverseJoinColumns = @JoinColumn(name = "site_user_id")
    )
    private Set<SiteUser> liker; // '좋아요'를 누른 사용자 목록
}
