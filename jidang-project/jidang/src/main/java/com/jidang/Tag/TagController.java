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
     * GET /tag/list/{tagName}
     * 특정 태그가 달린 게시물 목록을 HTML 템플릿으로 반환합니다.
     */
    @GetMapping("/list/{tagName}") // URL 경로를 REST API 형태에서 HTML 뷰 형태로 변경
    public String getPostsByTag(@PathVariable String tagName, Model model) {

        // 1. TagService를 호출하여 게시물 목록을 가져옵니다.
        List<Post> posts = tagService.findPostsByTagName(tagName);

        // 2. 템플릿에 전달할 데이터를 Model 객체에 담습니다.
        model.addAttribute("posts", posts);
        model.addAttribute("tagName", tagName); // 현재 태그 이름도 전달

        // 3. 반환 값은 HTML 템플릿 파일의 이름입니다.
        //    예: src/main/resources/templates/tag_post_list.html 파일을 찾아서 렌더링합니다.
        return "tag_post_list";
    }
}
