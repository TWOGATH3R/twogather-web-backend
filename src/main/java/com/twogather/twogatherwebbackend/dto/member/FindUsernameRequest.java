package com.twogather.twogatherwebbackend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindUsernameRequest {
    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z]+$",  message = "이름은 한글 혹은 영어로만 입력해주세요.")
    private String name;

}
