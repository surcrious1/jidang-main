package com.jidang.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Integer>{
    // 특정 Post에 연결된 모든 PostTag를 찾을 때 사용할 수 있습니다.
    // List<PostTag> findByPostId(Long postId);
}
