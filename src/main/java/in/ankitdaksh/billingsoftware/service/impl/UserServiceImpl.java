package in.ankitdaksh.billingsoftware.service.impl;

import in.ankitdaksh.billingsoftware.entity.UserEntity;
import in.ankitdaksh.billingsoftware.io.UserRequest;
import in.ankitdaksh.billingsoftware.io.UserResponse;
import in.ankitdaksh.billingsoftware.repository.UserRepository;
import in.ankitdaksh.billingsoftware.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest request) {
        UserEntity newUser = convertToEntity(request);
        System.out.println("Arkadeep1");
        // Save entity in repository
        newUser = userRepository.save(newUser);
        System.out.println("Arkadeep2");
        // Convert saved entity back to response DTO
        return convertToResponse(newUser);
    }

    private UserEntity convertToEntity(UserRequest request) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) //Encode password
                .role(request.getRole().toUpperCase()) //Ensure role is uppercase
                .name(request.getName())
                .build();
    }

    private UserResponse convertToResponse(UserEntity newUser) {
        return UserResponse.builder()
                .name(newUser.getName())
                .email(newUser.getEmail())
                .userId(newUser.getUserId())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .role(newUser.getRole())
                .build();
    }

    @Override
    public String getUserRole(String email) {
        UserEntity existingUser =userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for the email: "+email));
        return existingUser.getRole();
    }
    @Override
    public List<UserResponse> readUsers() {
        return userRepository.findAll()              // List<UserEntity>
                .stream()                            // Stream<UserEntity>
                .map(this::convertToResponse)        // convert each entity to response
                .collect(Collectors.toList());       // collect as List<UserResponse>
    }

    @Override
    public void deleteUser(String id) {
        UserEntity existingUser = userRepository.findByUserId(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.delete(existingUser);
    }
}
