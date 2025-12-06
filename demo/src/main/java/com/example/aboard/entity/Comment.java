package com.example.aboard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends IpTrackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Builder
    public Comment(Board board, String content, String nickname, String password, String ipAddress) {
        this.board = board;
        this.content = content;
        this.nickname = nickname;
        this.password = password;
        this.ipAddress = ipAddress;
    }
}
