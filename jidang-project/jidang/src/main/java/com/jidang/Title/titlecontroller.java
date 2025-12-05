package com.jidang.Title;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

import com.jidang.user.UserService;
import com.jidang.user.SiteUser;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@RequestMapping("/title")
@RequiredArgsConstructor
@Controller
public class TitleController {
    // TitleController.java (가정)

    // @RequiredArgsConstructor 또는 생성자 주입으로 UserService 주입 필요
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/titlepage") // 💡 칭호 선택 페이지의 URL 경로
    public String showTitlePage(Principal principal, Model model) {

        // 1. 현재 사용자 정보 조회
        String username = principal.getName();
        SiteUser siteUser = this.userService.getUser(username);

        // 2. Model에 사용자 정보 전달
        // 템플릿에서 ${user.titles}와 ${user.selectedTitle}에 접근하기 위해 필요합니다.
        model.addAttribute("user", siteUser);

        // 3. 템플릿 반환
        // 'titlepage.html' 파일을 찾아서 사용자에게 보여줍니다.
        return "titlepage";
    }
}