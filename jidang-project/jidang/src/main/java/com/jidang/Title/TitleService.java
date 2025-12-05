package com.jidang.Title;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 다른 패키지에 있는 클래스들을 가져오기 위한 Import (필수)
import com.jidang.user.SiteUser;
import com.jidang.user.UserRepository;
import com.jidang.Post.PostRepository;
import com.jidang.Comments.CommentsRepository;

import java.util.Set;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class TitleService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentsRepository commentsRepository;

    // 칭호 상수 정의
    private static final String TITLE_STRATEGY_MASTER = "공략마스터";
    private static final String TITLE_COMMENT_KING = "수다쟁이";

    @Transactional
    public void checkAndGrantTitles(SiteUser user) {
        // 1. 유저의 칭호 목록이 비어있을 경우를 대비해 초기화함
        if (user.getTitles() == null) {
            user.setTitles(new HashSet<>());
        }
        Set<String> currentTitles = user.getTitles();

        // 2. '공략마스터' 칭호 체크 (이미 가지고 있지 않은 경우에만 검사)
        if (!currentTitles.contains(TITLE_STRATEGY_MASTER)) {
            // [핵심] PostRepository에 정의한 메서드 이름으로 조회함
            // 해석: 작성자(Author)가 user이고, 태그 이름(Tag Name)이 "공략"인 글의 개수
            long strategyCount = postRepository.countByAuthorAndPostTags_Tag_Name(user, "공략");
            
            if (strategyCount >= 5) {
                user.addTitle(TITLE_STRATEGY_MASTER);
            }
        }

        // 3. '수다쟁이' 칭호 체크 (댓글 20개 이상)
        if (!currentTitles.contains(TITLE_COMMENT_KING)) {
            long commentCount = commentsRepository.countByAuthor(user);
            
            if (commentCount >= 20) {
                user.addTitle(TITLE_COMMENT_KING);
            }
        }

        // 4. 칭호가 추가되었을 수 있으므로 변경된 유저 정보를 저장함
        userRepository.save(user);
    }
}