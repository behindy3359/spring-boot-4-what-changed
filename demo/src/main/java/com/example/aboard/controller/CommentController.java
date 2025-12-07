package com.example.aboard.controller;

import com.example.aboard.dto.CommentDto;
import com.example.aboard.service.CommentService;
import com.example.aboard.util.IpAddressUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long boardId) {
        List<CommentDto> comments = commentService.findByBoardId(boardId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<Long> createComment(
            @PathVariable Long boardId,
            @Valid @RequestBody CommentDto commentDto,
            HttpServletRequest request) {

        String ipAddress = IpAddressUtil.getClientIp(request);
        Long id = commentService.save(boardId, commentDto, ipAddress);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/{parentId}/reply")
    public ResponseEntity<Long> createReply(
            @PathVariable Long boardId,
            @PathVariable Long parentId,
            @Valid @RequestBody CommentDto commentDto,
            HttpServletRequest request) {

        String ipAddress = IpAddressUtil.getClientIp(request);

        try {
            Long id = commentService.saveReply(boardId, parentId, commentDto, ipAddress);
            return ResponseEntity.ok(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam String password) {

        try {
            commentService.delete(commentId, password);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
