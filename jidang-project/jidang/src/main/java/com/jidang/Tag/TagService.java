package com.jidang.Tag;

import com.jidang.Post.Post;
import com.jidang.PostTag.PostTag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;


    /**
     * 여러 태그 이름으로 게시물을 조회하는 새로운 메서드
     * @param tagNames 검색할 태그 이름 목록
     * @return 하나라도 해당 태그를 가진 Post 목록
     */
    @Transactional(readOnly = true)
    public List<Post> findPostsByTagNames(List<String> tagNames) {

        // TagRepository의 새로운 메서드를 호출 (아래 3번 참조)
        // PostTag 엔티티를 거치지 않고, Post를 바로 가져오는 JPQL 또는 QueryDSL이 효율적이지만,
        // 여기서는 Repository에 간단한 메서드를 추가하여 처리합니다.

        return tagRepository.findPostsByTagNames(tagNames);
    }

}
