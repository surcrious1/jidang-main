package com.jidang.Post;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import com.jidang.DataNotFoundException;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

import com.jidang.user.SiteUser;
import com.jidang.Game.Game;
import com.jidang.Game.GameRepository;
import com.jidang.Tag.Tag;
import com.jidang.Tag.TagRepository;
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

        // 3. Post 저장 (PostTag 목록도 Cascade 설정에 의해 함께 저장됨)
        return postRepository.save(newPost);
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
    public Post create(String subject, String content, SiteUser user, List<String> tagNames, MultipartFile file) throws Exception {

        Post newPost = new Post();
        newPost.setSubject(subject);
        newPost.setContent(content);
        newPost.setCreateDate(LocalDateTime.now());
        newPost.setAuthor(user);

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

        return postRepository.save(newPost);
    }
}
