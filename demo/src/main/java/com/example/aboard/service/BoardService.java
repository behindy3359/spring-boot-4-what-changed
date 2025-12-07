package com.example.aboard.service;

import com.example.aboard.dto.BoardRequestDto;
import com.example.aboard.dto.BoardResponseDto;
import com.example.aboard.entity.Board;
import com.example.aboard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<BoardResponseDto> findAll(Pageable pageable){
        return boardRepository.findAll(pageable).map(BoardResponseDto::new);
    }

    @Transactional
    public BoardResponseDto findById(Long id){
        Board board = boardRepository
                .findById(id)
                .orElseThrow(
                        ()-> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        board.increaseViewCount();
        return new BoardResponseDto(board);
    }

    @Transactional
    public Long save(BoardRequestDto requestDto, String ipAddress){
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        Board board = requestDto.toEntity(encodedPassword, ipAddress);
        return boardRepository.save(board).getId();
    }

    public boolean checkPassword(Long id, String rawPassword){
        Board board = boardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("해당 게시글이 없습니다. id =" + id));

        return passwordEncoder.matches(rawPassword, board.getPassword());
    }

    @Transactional
    public Long update(Long id, BoardRequestDto requestDto, String password){
        Board board = boardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("해당 게시글이 없습니다. id =" + id));

        if(!passwordEncoder.matches(password, board.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        board.update(requestDto.getTitle(), requestDto.getContent());
        return id;
    }

    @Transactional
    public void delete(Long id, String password){
        Board board = boardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("해당 게시글이 없습니다. id =" + id));

        if(!passwordEncoder.matches(password, board.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        boardRepository.delete(board);
    }

    public Page<BoardResponseDto> search(String keyword, Pageable pageable){
        return boardRepository.searchByTitleOrContent(keyword, pageable).map(BoardResponseDto::new);
    }
}
