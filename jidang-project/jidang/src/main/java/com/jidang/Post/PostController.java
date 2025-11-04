package com.jidang.Post;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;//html파일을 띄우려면 이것도 없애도 됨

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import com.jidang.Comments.CommentsForm;

import java.security.Principal;
import com.jidang.user.SiteUser;
import com.jidang.user.UserService;

import org.springframework.security.access.prepost.PreAuthorize;//로그아웃 상태면 principal가 널이라서 오류뜨는걸 해결


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/post")
@RequiredArgsConstructor
@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Post> postList = this.postService.getList();
        model.addAttribute("postList", postList);
        return "searchlist"; //화면에 post list 문구 테스트 출력
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id,CommentsForm commentsForm) {
        Post post = this.postService.getPost(id);
        model.addAttribute("post", post);
        return "searchlist";
    }

    @ResponseBody //아직 질문등록 html없음
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String postCreate(PostForm postForm) {
        return "post_form";
    }

    
    //게시물 생성
    @ResponseBody //아직 Post_form html없음
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String postCreate(@Valid PostForm postForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "Post_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.postService.create(postForm.getSubject(), postForm.getContent(),siteUser); //질문저장
        return "redirect:/post/list"; // 질문 저장후 질문목록으로 이동
    }

    //게시물 수정
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String postModify(PostForm postForm, @PathVariable("id") Integer id, Principal principal) {
        Post post = this.postService.getPost(id);
        if(!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        postForm.setSubject(post.getSubject());
        postForm.setContent(post.getContent());
        return "Post_form";
    }

    //수정된 게시물 저장(Post형식으로 받기)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid PostForm postForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "Post_form";
        }
        Post question = this.postService.getPost(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.postService.modify(question, postForm.getSubject(), postForm.getContent());
        return String.format("redirect:/post/detail/%s", id);
    }

    //게시물 삭제
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Post post = this.postService.getPost(id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.postService.delete(post);
        return "redirect:/";
    }
}
