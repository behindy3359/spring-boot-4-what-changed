package com.example.aboard.repository;

import com.example.aboard.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBoardIdAndParentIsNullOrderByCreatedDateAsc(Long boardId);
    List<Comment> findByParentIdOrderByCreatedDateAsc(Long parentId);
}
