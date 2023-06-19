package com.twogather.twogatherwebbackend.dto.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class EmailRequest {
    @Email
    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    private String email;
}
