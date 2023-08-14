package com.erp.webtoon.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "사원 번호는 필수입니다.")
    private String employeeId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
