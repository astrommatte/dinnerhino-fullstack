package com.astrom.dinnerihno.user;

import com.astrom.dinnerihno.mapper.DtoMapper;
import com.astrom.dinnerihno.recipe.Recipe;
import com.astrom.dinnerihno.recipe.RecipeCreateDTO;
import com.astrom.dinnerihno.recipe.RecipeDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;

    @Value("${app.admin-username}")
    private String adminUsername;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public UserDTO createUser(CreateUserDTO dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActive(false);

        if (adminUsername.equalsIgnoreCase(dto.getUsername())) {
            user.setRole(UserRole.ADMIN);
            user.setActive(true);
        } else {
            user.setRole(UserRole.USER);
        }

        User saved = userRepository.save(user);

        return dtoMapper.toUserDto(saved);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(dtoMapper::toUserDto)
                .toList();
    }


    public UserDTO updateUser(Long id, CreateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setActive(dto.isActive());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User saved = userRepository.save(user);

        return dtoMapper.toUserDto(saved);
    }

    public UserDTO updateUserActive(Long id, CreateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(dto.isActive());

        User saved = userRepository.save(user);

        return dtoMapper.toUserDto(saved);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    //Till "mina sidor" för användaren som är inloggad.
    public UserDTO getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return dtoMapper.toUserDto(user);
    }

    //Till "mina sidor" för användaren som är inloggad.
    public UserDTO updatePersonalUser(Long userId, CreateUserDTO dto, String username) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        if (!user.getUsername().equals(username)) {
            throw new RuntimeException("You can only update your own user");
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User saved = userRepository.save(user);
        return dtoMapper.toUserDto(saved);
    }






















    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Användarnamn är username i ditt fall
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Logged-in user not found"));
    }
}

