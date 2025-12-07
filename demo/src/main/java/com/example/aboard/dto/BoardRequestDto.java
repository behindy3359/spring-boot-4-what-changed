package com.example.aboard.dto;

import com.example.aboard.entity.Board;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardRequestDto {

    @NotBlank(message = "제목을 입력해주세요")
    @Size(min = 2, max = 200, message = "제목은 2~200자 이내로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    @Size(min = 2, message = "2글자 이상의 내용이 필요합니다.")
    private String content;

    @NotBlank(message ="작성자를 밝혀주세요")
    @Size(min= 2, max = 50, message = "작성자는 2~50자 이내로 표현해주세요")
    private String nickname;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 4, max = 20, message = "비밀번호는 4~20자 이내로 입력해주세요.")
    private String password;

    public Board toEntity(String encodedPassword, String ipAddress){
        return Board.builder()
                .title(title)
                .content(content)
                .nickname(nickname)
                .password(encodedPassword)
                .ipAddress(ipAddress)
                .build();
    }
}
