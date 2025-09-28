package com.astrom.dinnerihno.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PasswordChangeDTO {
    private String newPassword;
}

