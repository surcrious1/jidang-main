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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

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

    //수정버튼 눌렀을 시 실행될 url
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String commentsModify(CommentsForm commentsForm, @PathVariable("id") Integer id, Principal principal) {
        Comments comments = this.commentsService.getcomments(id);
        if (!comments.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        commentsForm.setContent(comments.getContent());
        return "comments_form";
    }


    //답변 수정 처리
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String commentsModify(@Valid CommentsForm commentsForm, BindingResult bindingResult,
                               @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "comments_form";
        }
        Comments comments = this.commentsService.getcomments(id);
        if (!comments.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.commentsService.modify(comments, commentsForm.getContent());
        return String.format("redirect:/question/detail/%s", comments.getPost().getId());
    }

    //답변 삭제
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String commentsDelete(Principal principal, @PathVariable("id") Integer id) {
        Comments comments = this.commentsService.getcomments(id);
        if (!comments.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.commentsService.delete(comments);
        return String.format("redirect:/question/detail/%s", comments.getPost().getId());
    }
}
