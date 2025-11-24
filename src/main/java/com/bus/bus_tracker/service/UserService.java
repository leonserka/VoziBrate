package com.bus.bus_tracker.service;

import com.bus.bus_tracker.dto.UserRegisterDto;
import com.bus.bus_tracker.dto.UserLoginDto;
import com.bus.bus_tracker.dto.UserResponseDto;
import com.bus.bus_tracker.entity.UserEntity;
import com.bus.bus_tracker.repository.UserRepository;
import com.bus.bus_tracker.validator.UserValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserValidator userValidator, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto register(UserRegisterDto dto) {
        userValidator.validateRegister(dto);

        var user = new UserEntity();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("user");

        var savedUser = userRepository.save(user);

        return new UserResponseDto(savedUser.getName(), savedUser.getEmail(), savedUser.getRole());
    }

    public UserResponseDto login(UserLoginDto dto) {
        var userOpt = userRepository.findByEmail(dto.getEmail());

        var user = userOpt.filter(u -> passwordEncoder.matches(dto.getPassword(), u.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("Wrong user or password"));

        return new UserResponseDto(user.getName(), user.getEmail(), user.getRole());
    }


    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public void updateUserRole(Long userId, String role) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(role);
        userRepository.save(user);
    }

}
