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

import java.security.Principal; //로그인한 사용자의 정보를 알려면 스프링 시큐리티가 제공하는 Principal 객체를 사용


import com.jidang.user.SiteUser;
import com.jidang.user.UserService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import org.springframework.security.access.prepost.PreAuthorize; //로그아웃 상태면 principal가 널이라서 오류뜨는걸 해결

@RequestMapping("/comments")
@RequiredArgsConstructor
@Controller
public class CommentsController {
    private final PostService postService;
    private final CommentsService commentsService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createComments(Model model, @PathVariable("id") Integer id,
                                 @Valid CommentsForm commentsForm, BindingResult bindingResult,
                                 @RequestParam(value="content") String content,Principal principal) {
        Post post = this.postService.getPost(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            return "post_detail"; //아직 이 html없음
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());

        this.commentsService.create(post, commentsForm.getContent(),siteUser); //답변을 저장한다.
        return String.format("redirect:/post/detail/%s", id);
    }
}
