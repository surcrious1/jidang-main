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
}
