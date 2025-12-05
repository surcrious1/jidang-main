package com.jidang.Post;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import com.jidang.DataNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.jidang.Post.DTO.PostSearchCondition;
import org.springframework.data.jpa.domain.Specification;


import java.time.LocalDateTime;

import com.jidang.user.SiteUser;
import com.jidang.Game.Game;
import com.jidang.Game.GameRepository;
import com.jidang.Tag.Tag;
import com.jidang.Tag.TagRepository;
import com.jidang.Title.TitleService;
import com.jidang.PostTag.PostTag;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.util.UUID;
import java.io.File;


@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final GameRepository gameRepository;
    private final TagRepository tagRepository;
    private final TitleService titleService;

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
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.postRepository.save(q);
    }
    //tag기능 추가해서 create함수 오버로딩
    @Transactional //하나의 작업(트랜잭션)을 묶어서 실행하고, 문제가 생기면 전부 되돌리는 기능
    public Post create(String subject, String content, SiteUser user, List<String> tagNames) {

        // 1. Post 엔티티 생성 및 기본 속성 설정
        Post newPost = new Post();
        newPost.setSubject(subject);
        newPost.setContent(content);
        newPost.setCreateDate(LocalDateTime.now());
        newPost.setAuthor(user);

        // 2. 태그 처리 및 연결 (핵심 로직)
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {

                // 2-1. 태그 조회 또는 생성 후 저장
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));

                // 2-2. PostTag 연결 엔티티 생성
                // (PostTag.createPostTag 메서드가 Tag만 받도록 수정했다고 가정)
                PostTag postTag = PostTag.createPostTag(tag);

                // 2-3. Post의 편의 메서드를 호출하여 양방향 관계 설정 및 동기화
                newPost.addPostTag(postTag);
            }
        }

        Post savedPost = postRepository.save(newPost); // 글 저장 완료

        // ✅ 3. 글 저장이 끝난 직후 칭호 체크 실행
        titleService.checkAndGrantTitles(user);

        return savedPost;
    }


    //게임 이름으로 게시글 목록 조회
    public List<Post> getPostsByGameName(String gameName) {
        // 게임 이름으로 Game 객체 조회
        Game game = gameRepository.findByName(gameName)
                .orElseThrow(() -> new IllegalArgumentException("게임을 찾을 수 없습니다: " + gameName));

        // Game에 연결된 모든 게시글(Post) 반환
        return game.getPosts();
    }

    public void modify(Post post, String subject, String content) {
        post.setSubject(subject);
        post.setContent(content);
        post.setModifyDate(LocalDateTime.now());
        this.postRepository.save(post);
    }

    //게시물 삭제
    public void delete(Post post) {
        this.postRepository.delete(post);
    }

    //좋아요 추가
    public void like(Post post, SiteUser user) {
        post.getLiker().add(user); // Set에 사용자 추가
        this.postRepository.save(post);
    }
    
    //좋아요 취소 
    public void unlike(Post post, SiteUser user) {
        post.getLiker().remove(user); // Set에서 사용자 제거
        this.postRepository.save(post);
    }

    @Value("${file.upload.path}")
    private String uploadPath;

    // 태그 + 파일 업로드 지원 create 메서드
    @Transactional
    public Post create(String subject, String content, SiteUser user, List<String> tagNames, MultipartFile file, String gameSlug) throws Exception {

        Post newPost = new Post();
        newPost.setSubject(subject);
        newPost.setContent(content);
        newPost.setCreateDate(LocalDateTime.now());
        newPost.setAuthor(user);

        // 1. 💡 Game 엔티티 조회 및 설정
        Game game = gameRepository.findBySlug(gameSlug)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게임 Slug입니다: " + gameSlug));

        // 💡 Post에 Game 객체 설정
        newPost.setGame(game);

        // *** 파일 처리 로직 시작 ***
        if (file != null && !file.isEmpty()) {
            // 1. 파일명 중복 방지를 위한 UUID 생성
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();

            // 2. 파일 저장 (빈 껍데기 파일 생성 후 내용 전송)
            File saveFile = new File(uploadPath, fileName);
            file.transferTo(saveFile); // 실제 저장 실행

            // 3. 엔티티에 정보 저장
            newPost.setFilename(fileName);
            newPost.setFilepath("/uproads/" + fileName); // WebMvcConfig에서 설정한 경로 패턴 사용
        }
        // *** 파일 처리 로직 끝 ***

        // 기존 태그 처리 로직 (그대로 유지)
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                PostTag postTag = PostTag.createPostTag(tag);
                newPost.addPostTag(postTag);
            }
        }



        Post savedPost = postRepository.save(newPost); // 글 저장 완료

        // ✅ 3. 글 저장이 끝난 직후 칭호 체크 실행
        titleService.checkAndGrantTitles(user);

        return savedPost;
    }


    /**
     * 게시물 통합 검색 (키워드, 게임 종류, 태그)
     */
    @Transactional(readOnly = true)
    public List<Post> search(PostSearchCondition condition) {

        Specification<Post> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        // 1. 키워드 검색 조건 추가 (제목 또는 내용 OR 검색)
        if (condition.getKeyword() != null && !condition.getKeyword().isEmpty()) {
            spec = spec.and(searchByKeyword(condition.getKeyword()));
        }

        // 2. 게임 종류 검색 조건 추가 (AND 검색)
        if (condition.getGameType() != null && !condition.getGameType().isEmpty()) {
            spec = spec.and(searchByGameType(condition.getGameType()));
        }

        // 3. 태그 검색 조건 추가 (AND 검색)
        if (condition.getTags() != null && !condition.getTags().isEmpty()) {
            spec = spec.and(searchByTags(condition.getTags())); // 메서드 이름도 searchByTags로 변경
        }

        // 조합된 Specification을 사용하여 DB에서 최종 결과를 조회합니다.
        return postRepository.findAll(spec);
    }

    // ----------------------------------------------------
    // Specification 개별 정의 메서드
    // ----------------------------------------------------

    // 1. 제목 또는 내용으로 검색 (OR)
    private Specification<Post> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            String likeKeyword = "%" + keyword + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("subject"), likeKeyword),
                    criteriaBuilder.like(root.get("content"), likeKeyword)
            );
        };
    }

    // 2. 게임 종류로 검색 (Game 엔티티의 name으로 가정)
    private Specification<Post> searchByGameType(String gameTypeName) {
        return (root, query, criteriaBuilder) -> {
            // 'game' 필드를 통해 Game 엔티티로 조인하여 이름을 비교
            return criteriaBuilder.equal(root.get("game").get("name"), gameTypeName);
        };
    }

    // 3. 태그 이름으로 검색 (PostTag 엔티티를 통해 조인)
    private Specification<Post> searchByTags(List<String> tagNames) {
        return (root, query, criteriaBuilder) -> {

            // PostTag 엔티티를 통해 Tag 엔티티로 JOIN
            jakarta.persistence.criteria.Join<Object, Object> tagJoin = root.join("postTags").join("tag");

            // 💡 Tag 엔티티의 'name' 필드가 입력된 List<String> tagNames 중 하나라도 포함되는지 검사 (IN 절)
            return tagJoin.get("name").in(tagNames);
        };
    }

}
