package com.jidang.Post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostForm {
    @NotEmpty(message="제목은 필수항목입니다.") //Null 또는 빈 문자열("")을 허용하지 않음
    @Size(max=200)
    private String subject;

    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;

    @NotEmpty(message = "게임 종류는 필수 항목입니다.") // 폼에서 선택을 강제하기 위한 유효성 검사
    private String gameSlug;

    // HTML 폼에서 여러 태그 이름(String)을 List 형태로 받아올 필드입니다.
    private List<String> tagNames;

    // html form에서 name="file"로 넘어온 데이터를 받음
    private MultipartFile file;
}
