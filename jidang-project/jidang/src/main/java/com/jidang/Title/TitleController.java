package com.jidang.Title;

import com.jidang.user.SiteUser;
import com.jidang.user.UserService;
import com.jidang.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/title")
@RequiredArgsConstructor
@Controller
@PreAuthorize("isAuthenticated()")
public class TitleController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final TitleService titleService;

    /*
     GET /title
     - 네비게이션 "칭호" 메뉴
     - 마이페이지 "칭호 전체보기" 버튼
     두 곳에서 모두 이 페이지로 진입
    */
    @GetMapping("")
    public String showTitlePage(Principal principal, Model model) {

        // 1) 현재 로그인 사용자 조회
        String username = principal.getName();   // Security 설정에 맞는 값 (username/email)
        SiteUser user = this.userService.getUser(username);

        // 2) 혹시 새로 획득할 칭호가 있는지 체크
        titleService.checkAndGrantTitles(user);

        // 3) 업적(칭호) 목록 생성
        List<TitleInfo> earnedTitles = titleService.getEarnedTitleInfos(user);
        List<TitleInfo> lockedTitles = titleService.getLockedTitleInfos(user);

        // 4) 템플릿에서 사용할 모델 세팅
        model.addAttribute("user", user);
        model.addAttribute("earnedTitles", earnedTitles);
        model.addAttribute("lockedTitles", lockedTitles);
        model.addAttribute("selectedTitle", user.getSelectedTitle());

        // templates/titlepage.html
        return "titlepage";
    }

    /*
     * POST /title/select
     - 대표 칭호 선택 / 해제
    */
    @PostMapping("/select")
    public String selectTitle(@RequestParam(required = false) String title,
                              Principal principal) {

        String username = principal.getName();
        SiteUser user = this.userService.getUser(username);

        if (title == null || title.isBlank()) {
            // "선택 안 함"
            user.setSelectedTitle(null);
        } else {
            // 내가 가진 칭호 중 하나일 때만 대표 칭호로 설정
            if (user.getTitles().contains(title)) {
                user.setSelectedTitle(title);
            }
        }

        userRepository.save(user);

        // 대표 칭호 적용 후 마이페이지로 이동
        return "redirect:/user/mypage#title-section";
    }
}
