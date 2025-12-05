package com.jidang.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.jidang.user.SiteUser;

public interface PostRepository extends JpaRepository<Post, Integer>, JpaSpecificationExecutor<Post>{
    Post findBySubject(String subject);
    Post findBySubjectAndContent(String subject, String content);
    // 1. 특정 유저가 작성한 게시글 수 (이미 있으면 생략 가능)
    long countByAuthor(SiteUser author);

    // 2. 특정 유저가 작성하고 + 특정 태그가 포함된 게시글 수 조회
    // 네이밍 규칙: Author(작성자) AND PostTags_Tag_Name(게시글태그->태그->이름)
    
    long countByAuthorAndPostTags_Tag_Name(SiteUser author, String tagName);
}
