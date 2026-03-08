package com.omnichannel.center.domain.user;

import java.time.Instant;

public class RegistrationVerificationPolicy {
    public boolean canRegister(user_registration registration, String inputCode, String userName, String password) {
        if (registration == null || registration.getStatus() != RegistrationStatus.PENDING) {
            return false;
        }
        if (registration.getCodeExpiresAt() == null || registration.getCodeExpiresAt().isBefore(Instant.now())) {
            return false;
        }
        if (inputCode == null || !inputCode.equals(registration.getVerificationCode())) {
            return false;
        }
        if (userName == null || userName.isBlank()) {
            return false;
        }
        return password != null && !password.isBlank();
    }
}
