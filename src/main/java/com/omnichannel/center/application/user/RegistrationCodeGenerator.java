package com.omnichannel.center.application.user;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RegistrationCodeGenerator {
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateSixDigitCode() {
        int value = secureRandom.nextInt(1_000_000);
        return String.format("%06d", value);
    }
}
