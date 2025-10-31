package com.jidang.Post;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import com.jidang.DataNotFoundException;

import java.time.LocalDateTime;

import com.jidang.user.SiteUser;
import com.jidang.Game.Game;
import com.jidang.Game.GameRepository;


@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final GameRepository gameRepository;

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

    //게임 이름으로 게시글 목록 조회
    public List<Post> getPostsByGameName(String gameName) {
        // 게임 이름으로 Game 객체 조회
        Game game = gameRepository.findByName(gameName)
                .orElseThrow(() -> new IllegalArgumentException("게임을 찾을 수 없습니다: " + gameName));

        // Game에 연결된 모든 게시글(Post) 반환
        return game.getPosts();
    }

}
