package com.twogather.twogatherwebbackend.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentSaveUpdateRequest {
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private String content;
}
