package com.example.aboard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 45)
    private String ipAddress;

    @Builder
    public Comment(Board board, Comment parent, String content,
                   String nickname, String password, String ipAddress) {
        this.board = board;
        this.parent = parent;
        this.content = content;
        this.nickname = nickname;
        this.password = password;
        this.ipAddress = ipAddress;
    }

    public String getMaskedIp() {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return "Unknown";
        }
        String[] parts = ipAddress.split("\\.");
        if (parts.length == 4) {
            return parts[0] + ".xxx.xxx." + parts[3];
        }
        return ipAddress;
    }

    public String getIpColorCode() {
        if (ipAddress == null) return "#808080";
        int hash = ipAddress.hashCode();
        int r = Math.max((hash & 0xFF0000) >> 16, 100);
        int g = Math.max((hash & 0x00FF00) >> 8, 100);
        int b = Math.max(hash & 0x0000FF, 100);
        return String.format("#%02X%02X%02X", r, g, b);
    }

    public boolean isParentComment() {
        return this.parent == null;
    }

    public boolean isReply() {
        return this.parent != null;
    }

    public int getReplyCount() {
        return replies.size();
    }
}
