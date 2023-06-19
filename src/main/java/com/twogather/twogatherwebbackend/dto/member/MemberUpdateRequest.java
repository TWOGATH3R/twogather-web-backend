package com.twogather.twogatherwebbackend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MemberUpdateRequest {
    @NotEmpty
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{4,15}$",message = "아이디는 영어와 숫자를 포함해서 4자 이상 15자 이내로 입력해주세요.")
    private String username;

    @NotEmpty
    @Pattern(regexp = "^[가-힣a-zA-Z]*$",  message = "이름은 한글 혹은 영어로만 입력해주세요.")
    private String name;
}
