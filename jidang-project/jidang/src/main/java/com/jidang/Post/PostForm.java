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

    // 단 이미지와 내용중 하나는 반드시 있어야 한다. (서비스에 조건 부여)
    private String content;

    // 폼에서 기본으로 자유를 선택하기 때문에 선택을 강제하지 않아도 된다.
    private String gameSlug;

    // HTML 폼에서 여러 태그 이름(String)을 List 형태로 받아올 필드입니다.
    private List<String> tagNames;

    // html form에서 name="file"로 넘어온 데이터를 받음
    // List<~>를 사용하여 다중파일을 업로드 가능하게 함
    private List<MultipartFile> files;
}
