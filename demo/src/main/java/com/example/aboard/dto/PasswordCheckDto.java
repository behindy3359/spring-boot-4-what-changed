package com.example.aboard.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordCheckDto {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
