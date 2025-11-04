package com.jidang.Game;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


import com.jidang.Post.Post;

@RequiredArgsConstructor
@Service
public class GameService {
    private final GameRepository gameRepository;

    //게임목록 불러오기
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    //slug로 특정 게임을 찾는 메서드
    public Game getGameBySlug(String slug) {
        return gameRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("게임을 찾을 수 없습니다: " + slug));
    }

    //새 게임 데이터 db에 저장함수
    public Game createGame(Game game) {
        return gameRepository.save(game);
    }

    //게시글 조회
    public List<Post> getPostsByGameName(String gameName) {
        Game game = gameRepository.findByName(gameName)
                .orElseThrow(() -> new IllegalArgumentException("게임을 찾을 수 없습니다: " + gameName));
        return game.getPosts();
    }

}
