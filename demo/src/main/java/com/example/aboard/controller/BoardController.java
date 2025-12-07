package com.example.aboard.controller;

import com.example.aboard.dto.BoardRequestDto;
import com.example.aboard.dto.BoardResponseDto;
import com.example.aboard.dto.PasswordCheckDto;
import com.example.aboard.service.BoardService;
import com.example.aboard.util.IpAddressUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public String list(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<BoardResponseDto> boards;

        if (keyword != null && !keyword.trim().isEmpty()) {
            boards = boardService.search(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            boards = boardService.findAll(pageable);
        }

        model.addAttribute("boards", boards);
        return "board/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        BoardResponseDto board = boardService.findById(id);
        model.addAttribute("board", board);
        return "board/detail";
    }

    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("board", new BoardRequestDto());
        return "board/write";
    }

    @PostMapping("/write")
    public String write(
            @Valid @ModelAttribute("board") BoardRequestDto requestDto,
            BindingResult bindingResult,
            HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "board/write";
        }

        String ipAddress = IpAddressUtil.getClientIp(request);
        Long id = boardService.save(requestDto, ipAddress);

        return "redirect:/board/" + id;
    }

    @GetMapping("/{id}/edit")
    public String editPasswordForm(@PathVariable Long id, Model model) {
        model.addAttribute("boardId", id);
        model.addAttribute("passwordCheck", new PasswordCheckDto());
        return "board/password-check";
    }

    @PostMapping("/{id}/edit/check")
    public String checkPasswordForEdit(
            @PathVariable Long id,
            @Valid @ModelAttribute("passwordCheck") PasswordCheckDto passwordDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("boardId", id);
            return "board/password-check";
        }

        if (!boardService.checkPassword(id, passwordDto.getPassword())) {
            model.addAttribute("boardId", id);
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "board/password-check";
        }

        redirectAttributes.addFlashAttribute("password", passwordDto.getPassword());
        return "redirect:/board/" + id + "/edit/form";
    }

    @GetMapping("/{id}/edit/form")
    public String editForm(@PathVariable Long id, Model model) {
        BoardResponseDto board = boardService.findById(id);
        model.addAttribute("board", board);

        if (!model.containsAttribute("boardRequest")) {
            BoardRequestDto requestDto = new BoardRequestDto();
            requestDto.setTitle(board.getTitle());
            requestDto.setContent(board.getContent());
            model.addAttribute("boardRequest", requestDto);
        }

        return "board/edit";
    }

    @PostMapping("/{id}/edit/form")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute("boardRequest") BoardRequestDto requestDto,
            BindingResult bindingResult,
            @RequestParam String password,
            Model model) {

        if (bindingResult.hasErrors()) {
            BoardResponseDto board = boardService.findById(id);
            model.addAttribute("board", board);
            return "board/edit";
        }

        try {
            boardService.update(id, requestDto, password);
            return "redirect:/board/" + id;
        } catch (IllegalArgumentException e) {
            BoardResponseDto board = boardService.findById(id);
            model.addAttribute("board", board);
            model.addAttribute("error", e.getMessage());
            return "board/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {

        try {
            boardService.delete(id, password);
            return "redirect:/board";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/board/" + id;
        }
    }
}
