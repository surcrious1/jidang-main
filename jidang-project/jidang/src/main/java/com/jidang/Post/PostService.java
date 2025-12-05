package com.jidang.Post;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.io.File;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.jpa.domain.Specification;

import lombok.RequiredArgsConstructor;

import com.jidang.DataNotFoundException;
import com.jidang.Post.DTO.PostSearchCondition;
import com.jidang.user.SiteUser;
import com.jidang.Game.Game;
import com.jidang.Game.GameRepository;
import com.jidang.Tag.Tag;
import com.jidang.Tag.TagRepository;
import com.jidang.Title.TitleService;
import com.jidang.PostTag.PostTag;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final GameRepository gameRepository;
    private final TagRepository tagRepository;
    private final TitleService titleService;

    @Value("${file.upload.path}")
    private String uploadPath;

    /* =========================================
       기본 목록 / 단일 조회 / 생성 (옛 버전)
    ========================================= */

    public List<Post> getList() {
        return this.postRepository.findAll();
    }

    public Post getPost(Integer id) {
        Optional<Post> post = this.postRepository.findById(id);
        if (post.isPresent()) {
            return post.get();
        } else {
            throw new DataNotFoundException("post not found");
        }
    }

    public void create(String subject, String content, SiteUser user) {
        Post q = new Post();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(java.time.LocalDateTime.now());
        q.setAuthor(user);
        this.postRepository.save(q);
    }

    // tag 기능만 쓰던 예전 create
    @Transactional
    public Post create(String subject, String content, SiteUser user, List<String> tagNames) {

        Post newPost = new Post();
        newPost.setSubject(subject);
        newPost.setContent(content);
        newPost.setCreateDate(java.time.LocalDateTime.now());
        newPost.setAuthor(user);

        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {

                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));

                PostTag postTag = PostTag.createPostTag(tag);
                newPost.addPostTag(postTag);
            }
        }

        Post savedPost = postRepository.save(newPost);

        // 칭호 체크
        titleService.checkAndGrantTitles(user);

        return savedPost;
    }

    /* =========================================
       게임별 게시글 목록
    ========================================= */

    public List<Post> getPostsByGameName(String gameName) {
        Game game = gameRepository.findByName(gameName)
                .orElseThrow(() -> new IllegalArgumentException("게임을 찾을 수 없습니다: " + gameName));

        return game.getPosts();
    }

    /* =========================================
       수정 / 삭제 / 좋아요
    ========================================= */

    public void modify(Post post, String subject, String content) {
        post.setSubject(subject);
        post.setContent(content);
        post.setModifyDate(java.time.LocalDateTime.now());
        this.postRepository.save(post);
    }

    public void delete(Post post) {
        this.postRepository.delete(post);
    }

    public void like(Post post, SiteUser user) {
        post.getLiker().add(user);
        this.postRepository.save(post);
    }

    public void unlike(Post post, SiteUser user) {
        post.getLiker().remove(user);
        this.postRepository.save(post);
    }

    /* =========================================
       🔥 최신 create : 게임 + 태그 + 여러 이미지 업로드
    ========================================= */

    @Transactional
    public Post create(String subject,
                       String content,
                       SiteUser user,
                       List<String> tagNames,
                       List<MultipartFile> files,
                       String gameSlug) throws Exception {

        Post newPost = new Post();
        newPost.setSubject(subject);
        newPost.setContent(content);
        newPost.setCreateDate(java.time.LocalDateTime.now());
        newPost.setAuthor(user);

        // 0. 내용/이미지 최소 검증(둘중 하나는 반드시 존재해야된다.)
        boolean isContentEmpty = (content == null || content.trim().isEmpty());
        boolean isFilesEmpty = (files == null || files.isEmpty());

        if (isContentEmpty && isFilesEmpty) {
            throw new IllegalArgumentException("내용 또는 이미지는 최소 하나 이상 입력해야 합니다.");
        }

        // 1. 게임 설정
        // "자유"는 실제 게임이 아니므로 Game 엔티티 연결 안 함
        Game game = null;
        if (gameSlug != null && !gameSlug.isBlank() && !"자유".equals(gameSlug)) {
            game = gameRepository.findBySlug(gameSlug)
                    .orElseThrow(() ->
                            new IllegalArgumentException("존재하지 않는 게임 Slug입니다: " + gameSlug));
        }
        newPost.setGame(game);

        // 2. 태그 처리 - 비어 있으면 "자유" 기본값
        List<String> effectiveTags = new ArrayList<>();

        if (tagNames != null) {
            for (String t : tagNames) {
                if (t == null || t.isBlank()) continue;
                effectiveTags.add(t);
            }
        }

        if (gameSlug != null && !gameSlug.isBlank() && !"자유".equals(gameSlug)) {
            if (!effectiveTags.contains(gameSlug)) {   // 중복 방지
                effectiveTags.add(gameSlug);
            }
        }

        if (effectiveTags.isEmpty()) {
            effectiveTags.add("자유");
        }

        for (String tagName : effectiveTags) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));

            PostTag postTag = PostTag.createPostTag(tag);
            newPost.addPostTag(postTag);
        }

        // 3. 업로드 폴더 보장
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 4. 이미지 파일 여러 개 저장
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {

                if (file == null || file.isEmpty()) continue;

                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image")) {
                    // 이미지가 아니면 무시
                    continue;
                }

                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + file.getOriginalFilename();

                File saveFile = new File(uploadPath, fileName);
                file.transferTo(saveFile);

                PostImage postImage = PostImage.create(fileName, "/uploads/" + fileName);
                newPost.addImage(postImage);
            }
        }

        Post savedPost = postRepository.save(newPost);

        // 5. 칭호 체크
        titleService.checkAndGrantTitles(user);

        return savedPost;
    }

    /* =========================================
       검색 (키워드 / 게임 / 태그)
    ========================================= */

    @Transactional(readOnly = true)
    public List<Post> search(PostSearchCondition condition) {

        Specification<Post> spec = (root, query, cb) -> cb.conjunction();

        // 1. 키워드 검색
        if (condition.getKeyword() != null && !condition.getKeyword().isEmpty()) {
            spec = spec.and(searchByKeyword(condition.getKeyword()));
        }

        // 2. 게임 종류 검색
        if (condition.getGameType() != null && !condition.getGameType().isEmpty()) {
            spec = spec.and(searchByGameType(condition.getGameType()));
        }

        // 3. 태그 검색
        if (condition.getTags() != null && !condition.getTags().isEmpty()) {
            spec = spec.and(searchByTags(condition.getTags()));
        }

        return postRepository.findAll(spec);
    }

    // 1. 제목 또는 내용으로 검색 (OR)
    private Specification<Post> searchByKeyword(String keyword) {
        return (root, query, cb) -> {
            String likeKeyword = "%" + keyword + "%";
            return cb.or(
                    cb.like(root.get("subject"), likeKeyword),
                    cb.like(root.get("content"), likeKeyword)
            );
        };
    }

    // 2. 게임 종류로 검색 (Game.name 기준)
    private Specification<Post> searchByGameType(String gameTypeName) {
        return (root, query, cb) ->
                cb.equal(root.get("game").get("name"), gameTypeName);
    }

    // 3. 태그 이름으로 검색
    private Specification<Post> searchByTags(List<String> tagNames) {
        return (root, query, cb) -> {
            jakarta.persistence.criteria.Join<Object, Object> tagJoin =
                    root.join("postTags").join("tag");
            return tagJoin.get("name").in(tagNames);
        };
    }
}
