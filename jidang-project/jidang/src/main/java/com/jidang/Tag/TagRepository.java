package com.jidang.Tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer>{
    // 태그 이름으로 Tag 엔티티를 찾는 쿼리 메서드
    Optional<Tag> findByName(String name);
}
