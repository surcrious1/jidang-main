package com.jidang.Post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 저장된 파일명 (UUID 포함)
    @Column(nullable = false)
    private String filename;

    // 접근 가능한 파일 URL 경로
    @Column(nullable = false)
    private String filepath;

    // 다대일 관계 – 여러 이미지가 하나의 게시글에 속함
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    /*
     Post 엔티티와 연관관계를 맺는 편의 메서드
     (Post.addImage(PostImage image)에서도 호출됨)
    */
    public void setPostRelation(Post post) {
        this.post = post;
    }


    /*
     이미지 엔티티 생성용 정적 팩토리 메서드
    */
    public static PostImage create(String filename, String filepath) {
        PostImage image = new PostImage();
        image.setFilename(filename);
        image.setFilepath(filepath);
        return image;
    }

    // 없애도 문제 X
    @Override
    public String toString() {
        return "PostImage{id=" + id + ", filename='" + filename + "'}";
    }
}
