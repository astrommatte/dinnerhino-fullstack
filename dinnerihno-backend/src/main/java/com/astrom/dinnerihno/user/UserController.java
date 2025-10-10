package com.astrom.dinnerihno.user;

import com.astrom.dinnerihno.recipe.RecipeCreateDTO;
import com.astrom.dinnerihno.recipe.RecipeDTO;
import com.astrom.dinnerihno.setting.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SettingService settingService;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserDTO dto) {
        if (!settingService.isUserRegistrationAllowed()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Användarregistrering är inaktiverad.");
        }
        UserDTO created = userService.createUser(dto);
        return ResponseEntity.ok(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody CreateUserDTO dto) {
        UserDTO updatedUser = userService.updateUser(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/active")
    public ResponseEntity<UserDTO> updateUserActive(@PathVariable Long id, @RequestBody CreateUserDTO dto) {
        UserDTO updatedUser = userService.updateUserActive(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
//        return userService.findById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    //Endast admin
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 🔐 Endast admin
    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
                                               @RequestBody PasswordChangeDTO dto) {
        userService.changePassword(id, dto.getNewPassword());
        return ResponseEntity.noContent().build();
    }




    @GetMapping("/my-page")
    public ResponseEntity<UserDTO> getUser(Principal principal) {
        return ResponseEntity.ok(userService.getUser(principal.getName()));
    }

    @PutMapping("/my-page/{id}")
    public ResponseEntity<UserDTO> updatePersonalUser(@PathVariable Long id,
                                                  @RequestBody CreateUserDTO dto,
                                                  Principal principal) {
        return ResponseEntity.ok(userService.updatePersonalUser(id, dto, principal.getName()));
    }


}
