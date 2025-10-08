package com.astrom.dinnerihno.user;

import lombok.Data;

@Data
public class CreateUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean active;
}

