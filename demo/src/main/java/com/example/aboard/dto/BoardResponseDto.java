package com.example.aboard.dto;

import com.example.aboard.entity.Board;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class BoardResponseDto {

    private Long id;
    private String title;
    private String content;
    private String nickname;
    private String maskedIp;
    private String ipColorCode;
    private Long viewCount;
    private int commentCount;
    private String createdDate;
    private String modifiedDate;

    public BoardResponseDto(Board entity){

        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.nickname = entity.getNickname();
        this.maskedIp = entity.getMaskedIp();
        this.ipColorCode = entity.getIpColorCode();
        this.viewCount = entity.getViewCount();
        this.commentCount = entity.getComments().size();
        this.createdDate = toStringDateTime(entity.getCreatedDate());
        this.modifiedDate = toStringDateTime(entity.getModifiedDate());
    }

    private String toStringDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return localDateTime.format(formatter);
    }
}
