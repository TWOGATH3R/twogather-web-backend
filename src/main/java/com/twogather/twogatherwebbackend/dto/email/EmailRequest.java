package com.twogather.twogatherwebbackend.dto.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class EmailRequest {
    @Email
    private String email;
}
