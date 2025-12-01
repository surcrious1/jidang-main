package com.jidang.Post;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.jidang.user.SiteUser;

public interface PostRepository extends JpaRepository<Post, Integer>, JpaSpecificationExecutor<Post>{
    Post findBySubject(String subject);
    Post findBySubjectAndContent(String subject, String content);
    long countByAuthor(SiteUser author);
}
