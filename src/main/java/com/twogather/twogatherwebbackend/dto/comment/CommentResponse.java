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
    private Boolean isOwner;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createDate;

    public CommentResponse(Long commentId, String content, Boolean isOwner, LocalDate createDate) {
        this.commentId = commentId;
        this.content = content;
        this.isOwner = isOwner;
        this.createDate = createDate;
    }
}
