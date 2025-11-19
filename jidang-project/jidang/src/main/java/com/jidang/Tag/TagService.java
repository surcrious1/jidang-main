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
     * 특정 태그 이름으로 연결된 모든 게시물을 조회합니다.
     * @param tagName 검색할 태그 이름
     * @return 해당 태그가 포함된 Post 목록
     */
    @Transactional(readOnly = true)
    public List<Post> findPostsByTagName(String tagName) {

        Tag tag = tagRepository.findByName(tagName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다: " + tagName));

        //메서드 참조(Method Reference)로 변경하여 코드가 더 간결해짐
        return tag.getPostTags().stream()
                .map(PostTag::getPost) //PostTag 객체의 getPost() 메서드를 참조
                .collect(Collectors.toList());
    }
}
