package com.omnichannel.center.deployment.auth;

import com.omnichannel.center.application.auth.AuthApplicationService;
import com.omnichannel.center.application.auth.AuthResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthApplicationService authService;

    public AuthController(AuthApplicationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResult register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request.getEmail(), request.getPassword(), request.getFullName());
    }

    @PostMapping("/login")
    public AuthResult login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/refresh")
    public AuthResult refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request.getRefreshToken());
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return Map.of("message", "Logged out");
    }
}
