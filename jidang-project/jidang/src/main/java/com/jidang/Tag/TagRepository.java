package com.jidang.Tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.jidang.Post.Post;

import java.util.Optional;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer>{
    // 태그 이름으로 Tag 엔티티를 찾는 쿼리 메서드
    Optional<Tag> findByName(String name);

    /**
     * ✅ 여러 태그 이름 중 하나라도 가진 Post 목록을 찾는 쿼리
     * JPQL(Java Persistence Query Language)을 사용하여 PostTag를 조인합니다.
     * DISTINCT를 사용하여 중복 게시물을 제거합니다.
     */
    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN p.postTags pt " + // Post 엔티티의 postTags 필드를 조인
            "JOIN pt.tag t " +      // PostTag 엔티티의 tag 필드를 조인
            "WHERE t.name IN :tagNames")
    List<Post> findPostsByTagNames(@Param("tagNames") List<String> tagNames);
}
