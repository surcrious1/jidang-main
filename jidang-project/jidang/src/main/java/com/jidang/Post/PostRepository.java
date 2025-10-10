package com.jidang.Post;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer>{
    Post findBySubject(String subject);
    Post findBySubjectAndContent(String subject, String content);
}
