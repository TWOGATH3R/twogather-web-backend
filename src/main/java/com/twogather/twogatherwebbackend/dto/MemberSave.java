package com.twogather.twogatherwebbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class MemberSave {
    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,20}$", message = "비밀번호는 영어와 숫자를 포함해서 8자 이상 20자 이내로 입력해주세요.")
    private String password;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z]+$",  message = "이름은 한글 혹은 영어로만 입력해주세요.")
    private String name;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Pattern(regexp = "^01([0|1|6|7|8|9])(\\d{3}|\\d{4})(\\d{4})$", message = "핸드폰 번호는 010, 011, 016, 017, 018, 019로 시작하는 10~11자리 번호입니다.")
    private String phone;

}
