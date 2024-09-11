package de.vd40xu.smilebase.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponseDTO {
    private String token;
    private long expiresIn;
}
