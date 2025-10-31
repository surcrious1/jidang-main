package com.jidang.Game;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.ResponseBody;//html파일을 띄우려면 이것도 없애도 됨

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.ui.Model;

import com.jidang.Post.Post;
import com.jidang.Post.PostService;

@RequestMapping("/game")
@RequiredArgsConstructor
@Controller
public class GameController {

    private final GameService gameService;
    private final PostService postService;

    //전체 게임 목록 조회
    @GetMapping("/list")
    public List<Game> getAllGames() {
        return gameService.getAllGames();
    }

    //slug로 게임 조회
    @GetMapping("/{slug}")
    public Game getGameBySlug(@PathVariable String slug) {
        return gameService.getGameBySlug(slug);
    }

    //새 게임 등록
    @PostMapping("/create")
    public Game createGame(@RequestBody Game game) {
        return gameService.createGame(game);
    }

    //특정 게임 게시물 조회
    @GetMapping("/{gameName}")
    public String getPostsByGame(@PathVariable String gameName, Model model) {
        // Service에서 게임 이름으로 게시글 조회
        List<Post> posts = postService.getPostsByGameName(gameName);

        model.addAttribute("posts", posts);       // 게시글 목록 전달
        model.addAttribute("gameName", gameName); // 게임 이름 전달
        return "contentpage";                   // templates/posts_by_game.html
    }
}
