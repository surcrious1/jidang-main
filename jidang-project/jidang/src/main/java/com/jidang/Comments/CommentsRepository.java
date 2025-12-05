package com.jidang.Comments;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jidang.user.SiteUser;

public interface CommentsRepository extends JpaRepository<Comments, Integer>{
    // 특정 유저가 작성한 댓글 수 카운트
    long countByAuthor(SiteUser author);
}