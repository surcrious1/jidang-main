package com.jidang.Title;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jidang.user.SiteUser;
import com.jidang.user.UserRepository;
import com.jidang.Post.PostRepository;
import com.jidang.Comments.CommentsRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TitleService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentsRepository commentsRepository;

    // Enum 에서 이름을 가져오도록 변경 (한 곳에서만 정의되게)
    private static final String TITLE_STRATEGY_MASTER =
            TitleInfo.STRATEGY_MASTER.getDisplayName();
    private static final String TITLE_COMMENT_KING =
            TitleInfo.COMMENT_KING.getDisplayName();

    /*
     유저의 현재 글/댓글 수를 보고 칭호를 부여하는 로직
     - 공략마스터: 공략 태그 글 5개 이상
     - 수다쟁이  : 댓글 20개 이상
    */
    @Transactional
    public void checkAndGrantTitles(SiteUser user) {
        if (user.getTitles() == null) {
            user.setTitles(new HashSet<>());
        }
        Set<String> currentTitles = user.getTitles();

        // 1) 공략마스터 입수 조건
        if (!currentTitles.contains(TITLE_STRATEGY_MASTER)) {
            long strategyCount =
                    postRepository.countByAuthorAndPostTags_Tag_Name(user, "공략");
            if (strategyCount >= 5) {
                user.addTitle(TITLE_STRATEGY_MASTER);
            }
        }

        // 2) 수다쟁이 입수 조건
        if (!currentTitles.contains(TITLE_COMMENT_KING)) {
            long commentCount = commentsRepository.countByAuthor(user);
            if (commentCount >= 20) {
                user.addTitle(TITLE_COMMENT_KING);
            }
        }

        // 변경된 칭호 저장
        userRepository.save(user);
    }

    /*
     사용자가 가지고 있는 문자열 titles -> TitleInfo 리스트로 변환
     (획득한 업적 목록)
     */
    public List<TitleInfo> getEarnedTitleInfos(SiteUser user) {
        return user.getTitles().stream()
                .map(TitleInfo::fromDisplayName)
                .filter(Objects::nonNull)   // Enum 에 등록되지 않은 값은 제외
                .collect(Collectors.toList());
    }

    /*
     전체 TitleInfo 중, 아직 가지지 않은 것들
     (미획득 업적 목록)
    */
    public List<TitleInfo> getLockedTitleInfos(SiteUser user) {
        Set<String> owned = user.getTitles();
        return Stream.of(TitleInfo.values())
                .filter(t -> !owned.contains(t.getDisplayName()))
                .collect(Collectors.toList());
    }
}
