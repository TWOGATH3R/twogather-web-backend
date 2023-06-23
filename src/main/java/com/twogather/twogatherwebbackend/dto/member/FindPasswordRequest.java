package com.twogather.twogatherwebbackend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindPasswordRequest {
    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{4,15}$",message = "아이디는 영어와 숫자를 포함해서 4자 이상 15자 이내로 입력해주세요.")
    private String username;
}
