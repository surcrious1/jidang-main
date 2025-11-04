package com.jidang.Comments;

import com.jidang.Post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import com.jidang.user.SiteUser;

import java.util.Optional;
import com.jidang.DataNotFoundException;

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

    //답변 조회(수정을 위한)
    public Comments getcomments(Integer id) {
        Optional<Comments> comments = this.commentsRepository.findById(id);
        if (comments.isPresent()) {
            return comments.get();
        } else {
            throw new DataNotFoundException("comments not found");
        }
    }

    //답변 수정
    public void modify(Comments comments, String content) {
        comments.setContent(content);
        comments.setModifyDate(LocalDateTime.now());
        this.commentsRepository.save(comments);
    }

    //답변 삭제
    public void delete(Comments comments) {
        this.commentsRepository.delete(comments);
    }
}
