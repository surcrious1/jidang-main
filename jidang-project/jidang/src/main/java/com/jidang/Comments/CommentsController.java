package com.jidang.Comments;

import com.jidang.Post.Post;
import com.jidang.Post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/comments")
@RequiredArgsConstructor
@Controller
public class CommentsController {
    private final PostService postService;
    private final CommentsService commentsService;

    @PostMapping("/create/{id}")
    public String createComments(Model model, @PathVariable("id") Integer id, @RequestParam(value="content") String content) {
        Post post = this.postService.getPost(id);
        this.commentsService.create(post, content); //답변을 저장한다.
        return String.format("redirect:/post/detail/%s", id);
    }
}
