package com.omnichannel.center.application.user;

import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class RegistrationLinkBuilder {
    public String buildPasswordSetupLink(String baseUrl, String token, String sixDigitCode) {
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String encodedCode = URLEncoder.encode(sixDigitCode, StandardCharsets.UTF_8);
        return baseUrl + "/password-setup?token=" + encodedToken + "&code=" + encodedCode;
    }
}
