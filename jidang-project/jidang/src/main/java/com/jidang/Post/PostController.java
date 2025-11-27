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
import org.springframework.web.multipart.MultipartFile;

import com.jidang.Post.DTO.PostSearchCondition;

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

    //@ResponseBody //아직 질문등록 html없음
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String postCreate(PostForm postForm) {
        return "post_form";
    }

    
    //게시물 생성 (파일 업로드 기능 추가)
    // PostController.java (수정된 postCreate 함수)
    // @ResponseBody //아직 Post_form html없음
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String postCreate(@Valid PostForm postForm, BindingResult bindingResult, Principal principal) throws Exception {
        
        // 1. 유효성 검사 (기존과 동일)
        if (bindingResult.hasErrors()) {
            return "post_form"; // 템플릿 이름이 대소문자 구분되므로 post_form으로 통일 권장
        }

        // 2. 작성자 정보 가져오기 (기존과 동일)
        SiteUser siteUser = this.userService.getUser(principal.getName());

        // 3. 폼에서 파일 꺼내기 (추가된 부분)
        MultipartFile file = postForm.getFile();

        // 4. 서비스 호출 (태그와 파일을 한 번에 넘기도록 수정)
        // PostService의 create 메서드 파라미터 순서와 일치해야 함
        this.postService.create(
                postForm.getSubject(),
                postForm.getContent(),
                siteUser,
                postForm.getTagNames(), // 태그 리스트
                file                    // 파일 객체
        );

        // 5. 리다이렉트
        return "redirect:/post/list";
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

    //좋아요 URL 추가
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/like/{id}")
    public String postLike(Principal principal, @PathVariable("id") Integer id) {
        Post post = this.postService.getPost(id);
        SiteUser user = this.userService.getUser(principal.getName());

        // 이미 '좋아요'를 눌렀는지 확인
        if (post.getLiker().contains(user)) {
            this.postService.unlike(post, user); // 눌렀으면 취소
        } else {
            this.postService.like(post, user); // 안 눌렀으면 추가
        }
        
        return String.format("redirect:/post/detail/%s", id);
    }


    /**
     * 통합 검색 엔드포인트
     * URL 예시: /post/search?keyword=공략&gameType=롤&tags=꿀팁&tags=재미
     */
    @GetMapping("/search")
    public String searchPosts(PostSearchCondition condition, Model model) {

        // 1. Service의 통합 검색 메서드 호출
        List<Post> searchResults = postService.search(condition);

        // 2. 뷰에 데이터 전달
        model.addAttribute("posts", searchResults);
        model.addAttribute("searchCondition", condition); // 뷰에서 검색어 유지를 위해 전달

        return "post_list"; // 게시물 데이터(posts)를 반복문으로 출력하도록 구성된 템플릿의 이름
    }


}
