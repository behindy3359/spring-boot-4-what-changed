package com.example.aboard.service;

import com.example.aboard.dto.CommentDto;
import com.example.aboard.entity.Board;
import com.example.aboard.entity.Comment;
import com.example.aboard.repository.BoardRepository;
import com.example.aboard.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final PasswordEncoder passwordEncoder;

    public List<CommentDto> findByBoardId(Long boardId) {
        return commentRepository.findByBoardIdAndParentIsNullOrderByCreatedDateAsc(boardId)
                .stream()
                .map(comment -> new CommentDto(comment, true))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long save(Long boardId, CommentDto commentDto, String ipAddress) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        String encodedPassword = passwordEncoder.encode(commentDto.getPassword());
        Comment comment = commentDto.toEntity(board, encodedPassword, ipAddress);

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public Long saveReply(Long boardId, Long parentId, CommentDto commentDto, String ipAddress) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));

        if (parent.isReply()) {
            throw new IllegalArgumentException("대댓글에는 댓글을 달 수 없습니다.");
        }

        String encodedPassword = passwordEncoder.encode(commentDto.getPassword());
        Comment reply = commentDto.toReplyEntity(board, parent, encodedPassword, ipAddress);

        return commentRepository.save(reply).getId();
    }

    @Transactional
    public void delete(Long id, String password) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));

        if (!passwordEncoder.matches(password, comment.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (comment.isParentComment() && !comment.getReplies().isEmpty()) {
            throw new IllegalArgumentException("대댓글이 있는 댓글은 삭제할 수 없습니다.");
        }

        commentRepository.delete(comment);
    }
}