package com.jidang.Post;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import com.jidang.Comments.CommentsForm;

import java.security.Principal;
import com.jidang.user.SiteUser;
import com.jidang.user.UserService;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import com.jidang.Post.DTO.PostSearchCondition;

import org.springframework.web.bind.annotation.RequestParam;

// 🔹 DB에서 게임 정보를 가져오기 위해 추가
import com.jidang.Game.Game;
import com.jidang.Game.GameRepository;

@RequestMapping("/post")
@RequiredArgsConstructor
@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final GameRepository gameRepository;   // 🔹 추가

    /* ============================================================
       ① 기존 리스트 (테스트 페이지)
       ============================================================ */
    @GetMapping("/list")
    public String list(Model model) {
        List<Post> postList = this.postService.getList();
        model.addAttribute("posts", postList);
        return "community";
    }

    /* ============================================================
       ② 상세 페이지
       ============================================================ */
    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, CommentsForm commentsForm,Principal principal) {

        Post post = this.postService.getPost(id);  // ← Post 엔티티 1개 조회

        model.addAttribute("post", post);          // ← 상세 페이지에서 사용할 데이터
        model.addAttribute("commentsForm", commentsForm); // 나중에 댓글 기능에 사용 예정

        if (principal != null) {
            model.addAttribute("principal", principal);
        }
        return "postdetail";
    }

    /* ============================================================
       ③ 게시물 작성 화면
       ============================================================ */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String postCreate(
            PostForm postForm,
            @RequestParam(value = "gameSlug", required = false) String gameSlug) {

        // 게임 페이지에서 넘어온 경우: 해당 slug 사용
        if (gameSlug != null && !gameSlug.isBlank()) {
            postForm.setGameSlug(gameSlug);
        } else {
            // 아무 것도 없으면 기본값 "자유"
            if (postForm.getGameSlug() == null || postForm.getGameSlug().isBlank()) {
                postForm.setGameSlug("자유");
            }
        }
        return "post_form";
    }

    /* ============================================================
       ④ 게시물 생성 (파일 + 태그 + 게임 종류)
       ============================================================ */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String postCreate(
            @Valid PostForm postForm,
            BindingResult bindingResult,
            Principal principal) throws Exception {

        if (bindingResult.hasErrors()) {
            return "post_form";
        }

        SiteUser siteUser = this.userService.getUser(principal.getName());
        List<MultipartFile> files = postForm.getFiles();
        String gameSlug = postForm.getGameSlug();

        // 저장된 Post 객체를 그대로 받아온다
        Post savedPost = this.postService.create(
                postForm.getSubject(),
                postForm.getContent(),
                siteUser,
                postForm.getTagNames(),
                files,
                gameSlug
        );

        // 방금 작성한 글의 상세 페이지로 이동
        return "redirect:/post/detail/" + savedPost.getId();
    }

    /* ============================================================
       ⑤ 게시물 수정
       ============================================================ */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String postModify(PostForm postForm, @PathVariable("id") Integer id, Principal principal) {
        Post post = this.postService.getPost(id);

        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }

        postForm.setSubject(post.getSubject());
        postForm.setContent(post.getContent());
        return "post_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String postModify(
            @Valid PostForm postForm,
            BindingResult bindingResult,
            Principal principal,
            @PathVariable("id") Integer id) {

        if (bindingResult.hasErrors()) {
            return "post_form";
        }

        Post post = this.postService.getPost(id);

        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }

        this.postService.modify(post, postForm.getSubject(), postForm.getContent());
        return String.format("redirect:/post/detail/%s", id);
    }

    /* ============================================================
       ⑥ 게시물 삭제
       ============================================================ */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String postDelete(Principal principal, @PathVariable("id") Integer id) {
        Post post = this.postService.getPost(id);

        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }

        this.postService.delete(post);
        return "redirect:/post/community";
    }

    /* ============================================================
       ⑦ 좋아요 toggle
       ============================================================ */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/like/{id}")
    public String postLike(Principal principal, @PathVariable("id") Integer id) {
        Post post = this.postService.getPost(id);
        SiteUser user = this.userService.getUser(principal.getName());

        if (post.getLiker().contains(user)) {
            this.postService.unlike(post, user);
        } else {
            this.postService.like(post, user);
        }

        return String.format("redirect:/post/detail/%s", id);
    }

    /* ============================================================
       ⑧ 커뮤니티 전체 게시물 페이지 (네비 연결)
       URL: /post/community
       ============================================================ */
    @GetMapping("/community")
    public String community(PostSearchCondition condition, Model model) {

        // null-safe
        condition.setTags(null);

        List<Post> posts = postService.search(condition);

        model.addAttribute("posts", posts);
        model.addAttribute("activePage", "community");

        return "community";
    }

    /* ============================================================
   ⑨ 태그별 게시물 목록 (게임 slug + 태그 검색 + DB 게임정보)
   URL: /post/tag/{slug}  (slug = game.slug)
   ============================================================ */
    @GetMapping("/tag/{slug}")
    public String listByTag(@PathVariable("slug") String slug,
                            PostSearchCondition condition,
                            Model model) {

        // 1) slug(영어 코드) -> 태그 이름(한글) 매핑
        String tagName = switch (slug) {
            case "genshin"     -> "원신";
            case "limbus"      -> "림버스 컴퍼니";
            case "starrail"    -> "스타레일";
            case "re1999"      -> "리버스 1999";
            case "bluearchive" -> "블루아카이브";
            default            -> slug;   // 자유, 공략, 정보 등 이미 한글인 경우
        };

        // 2) 태그 조건 세팅 (검색용)
        if (condition.getTags() == null) {
            condition.setTags(new ArrayList<>());
        } else {
            condition.getTags().clear();
        }
        condition.getTags().add(tagName);   // 이제 한글 태그 이름으로 검색

        // 3) 게시물 검색
        List<Post> posts = postService.search(condition);
        model.addAttribute("posts", posts);

        // 글쓰기 버튼 등에서 사용할 슬러그(영어 코드)
        model.addAttribute("currentTag", slug);

        // 4) 게임 정보는 DB에서 slug로 조회
        Game game = gameRepository.findBySlug(slug).orElse(null);
        if (game != null) {
            model.addAttribute("game", game);   // Game 엔티티 전체 전달
        }

        return "postlist_tag_page";
    }

    /* ============================================================
       ⑩ 검색 기능
       ============================================================ */
    @GetMapping("/search")
    public String searchPosts(PostSearchCondition condition, Model model) {

        List<Post> searchResults = postService.search(condition);

        model.addAttribute("posts", searchResults);
        model.addAttribute("searchCondition", condition);

        return "community";
    }

}
