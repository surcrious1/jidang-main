package com.jidang.Comments;

import com.jidang.Post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import com.jidang.user.SiteUser;

@RequiredArgsConstructor
@Service
public class CommentsService {
    private final CommentsRepository commentsRepository;


    public void create(Post post, String content,SiteUser author) {
        Comments comments = new Comments();
        comments.setContent(content);
        comments.setCreateDate(LocalDateTime.now());
        comments.setPost(post);
        comments.setAuthor(author);
        this.commentsRepository.save(comments);
    }
}
