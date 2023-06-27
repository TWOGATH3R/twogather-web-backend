package com.twogather.twogatherwebbackend.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class CommentResponse {
    private Long commentId;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createDate;

    public CommentResponse(Long commentId, String content, LocalDate createDate) {
        this.commentId = commentId;
        this.content = content;
        this.createDate = createDate;
    }
}
