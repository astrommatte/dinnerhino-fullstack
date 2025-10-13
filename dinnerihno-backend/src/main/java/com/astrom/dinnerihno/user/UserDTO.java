package com.astrom.dinnerihno.user;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private UserRole role;
    private boolean active;
}
