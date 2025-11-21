package com.jidang.Tag;

import org.springframework.stereotype.Controller;
import com.jidang.Post.Post;
import com.jidang.Tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;


    /**
     * 여러 태그로 조회하는 새로운 메서드 (쿼리 파라미터 사용)
     * URL 예시: /tag/search?tags=공략&tags=팁&tags=자바
     */
    @GetMapping("/search")
    public String getPostsByMultipleTags(
            // @RequestParam으로 List<String>을 받으면 자동으로 여러 파라미터를 리스트로 변환합니다.
            @RequestParam(value = "tags", required = false) List<String> tagNames,
            Model model
    ) {
        if (tagNames == null || tagNames.isEmpty()) {
            model.addAttribute("posts", List.of());
            model.addAttribute("tagName", "검색 태그 없음");
            return "tag_post_list";
        }

        // TagService의 새로운 메서드 호출 (아래 2번 참조)
        List<Post> posts = tagService.findPostsByTagNames(tagNames);

        model.addAttribute("posts", posts);
        // 검색 태그 목록을 템플릿에 보여주기 위해 쉼표로 연결
        model.addAttribute("tagName", String.join(", ", tagNames));

        return "tag_post_list";
    }
}
