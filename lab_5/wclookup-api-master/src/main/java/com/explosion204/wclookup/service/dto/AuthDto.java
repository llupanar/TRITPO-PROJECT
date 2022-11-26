package com.explosion204.wclookup.service.dto;

import com.explosion204.wclookup.service.validation.annotation.DtoClass;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DtoClass
public class AuthDto {
    private String accessToken;
    private String refreshToken;
}
