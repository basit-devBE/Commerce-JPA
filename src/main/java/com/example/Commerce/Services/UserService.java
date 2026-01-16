package com.example.Commerce.Services;

import com.example.Commerce.DTOs.*;
import com.example.Commerce.Entities.UserEntity;
import com.example.Commerce.Mappers.UserMapper;
import com.example.Commerce.Repositories.UserRepository;
import com.example.Commerce.errorHandlers.EmailAlreadyExists;
import com.example.Commerce.errorHandlers.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public LoginResponseDTO addUser(UserRegistrationDTO userDTO){

        Optional<UserEntity> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if(existingUser.isPresent()){
            throw new EmailAlreadyExists("Email already exists: " + userDTO.getEmail());
        } else {
            UserEntity userEntity = userMapper.toEntity(userDTO);
            
            String hashedPassword = BCrypt.hashpw(userEntity.getPassword(), BCrypt.gensalt());
            userEntity.setPassword(hashedPassword);

            UserEntity savedUser = userRepository.save(userEntity);
            return userMapper.toResponseDTO(savedUser);
        }
    }

    public LoginResponseDTO loginUser(LoginDTO loginDTO){
        log.info("Attempting login for email: {}", loginDTO.getEmail());
        Optional<UserEntity> userOpt = userRepository.findByEmail(loginDTO.getEmail());
        if(userOpt.isPresent()){
            UserEntity userEntity = userOpt.get();
            if(BCrypt.checkpw(loginDTO.getPassword(), userEntity.getPassword())){
                String randomString = UUID.randomUUID().toString();
                String token = randomString + "-" + userEntity.getId();
                LoginResponseDTO responseDTO = userMapper.toResponseDTO(userEntity);
                responseDTO.setToken(token);
                return responseDTO;
            } else {
                throw new IllegalArgumentException("Invalid password");
            }
        } else {
            throw new ResourceNotFoundException("User not found with email: " + loginDTO.getEmail());
        }
    }

    public userSummaryDTO findUserByEmail(String email){
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if(userOpt.isPresent()){
            return userMapper.toSummaryDTO(userOpt.get());
        } else {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
    }

    public userSummaryDTO findUserById(Long id){
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if(userOpt.isPresent()){
            return userMapper.toSummaryDTO(userOpt.get());
        } else {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }
    public userSummaryDTO updateUser(Long id, @Valid UpdateUserDTO userDTO){
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if(userDTO.getFirstName() != null && !userDTO.getFirstName().isBlank()){
            userEntity.setFirstName(userDTO.getFirstName());
        }
        if(userDTO.getLastName() != null && !userDTO.getLastName().isBlank()){
            userEntity.setLastName(userDTO.getLastName());
        }
        if(userDTO.getEmail() != null && !userDTO.getEmail().isBlank()){
            userEntity.setEmail(userDTO.getEmail());
        }
        UserEntity updatedUser = userRepository.save(userEntity);
        return userMapper.toSummaryDTO(updatedUser);
    }

    public Page<userSummaryDTO> getAllUsers(Pageable pageable){
      return userRepository.findAll(pageable).map(userMapper::toSummaryDTO);
    }

    public void deleteUser(Long id){
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(userEntity);
    }
}