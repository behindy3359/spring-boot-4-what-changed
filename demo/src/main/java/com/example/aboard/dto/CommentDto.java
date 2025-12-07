package com.example.aboard.dto;

import com.example.aboard.entity.Board;
import com.example.aboard.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {

    private Long id;

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 4, max = 20)
    private String password;

    private Long parentId;

    private String maskedIp;
    private String ipColorCode;
    private String createdDate;

    private List<CommentDto> replies = new ArrayList<>();
    private int replyCount;

    public Comment toEntity(Board board, String encodedPassword, String ipAddress) {
        return Comment.builder()
                .board(board)
                .parent(null)
                .content(content)
                .nickname(nickname)
                .password(encodedPassword)
                .ipAddress(ipAddress)
                .build();
    }

    public Comment toReplyEntity(Board board, Comment parent, String encodedPassword, String ipAddress) {
        return Comment.builder()
                .board(board)
                .parent(parent)
                .content(content)
                .nickname(nickname)
                .password(encodedPassword)
                .ipAddress(ipAddress)
                .build();
    }

    public CommentDto(Comment entity) {
        this.id = entity.getId();
        this.content = entity.getContent();
        this.nickname = entity.getNickname();
        this.maskedIp = entity.getMaskedIp();
        this.ipColorCode = entity.getIpColorCode();
        this.createdDate = toStringDateTime(entity.getCreatedDate());
        this.replyCount = entity.getReplyCount();

        if (entity.getParent() != null) {
            this.parentId = entity.getParent().getId();
        }
    }

    public CommentDto(Comment entity, boolean includeReplies) {
        this(entity);

        if (includeReplies && entity.isParentComment()) {
            this.replies = entity.getReplies().stream()
                    .map(reply -> new CommentDto(reply, false))
                    .collect(Collectors.toList());
        }
    }

    private String toStringDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return localDateTime.format(formatter);
    }
}
