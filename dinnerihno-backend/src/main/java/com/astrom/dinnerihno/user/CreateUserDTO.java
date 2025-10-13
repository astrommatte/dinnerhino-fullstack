package com.astrom.dinnerihno.user;

import lombok.Data;

@Data
public class CreateUserDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean active;
}

