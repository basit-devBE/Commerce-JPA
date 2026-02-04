package com.example.commerce.services;

import com.example.commerce.dtos.requests.LoginDTO;
import com.example.commerce.dtos.requests.UpdateUserDTO;
import com.example.commerce.dtos.requests.UserRegistrationDTO;
import com.example.commerce.dtos.responses.LoginResponseDTO;
import com.example.commerce.dtos.responses.userSummaryDTO;
import com.example.commerce.entities.UserEntity;
import com.example.commerce.errorhandlers.ResourceAlreadyExists;
import com.example.commerce.errorhandlers.ResourceNotFoundException;
import com.example.commerce.interfaces.IUserService;
import com.example.commerce.mappers.UserMapper;
import com.example.commerce.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @CacheEvict(value = {"userById", "userByEmail"}, allEntries = true)
    public LoginResponseDTO addUser(UserRegistrationDTO userDTO){

        Optional<UserEntity> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if(existingUser.isPresent()){
            throw new ResourceAlreadyExists("Email already exists: " + userDTO.getEmail());
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
                String randomString = UUID.randomUUID().toString().replace("-", "");
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

    @Cacheable(value = "userByEmail", key = "#email")
    public userSummaryDTO findUserByEmail(String email){
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if(userOpt.isPresent()){
            return userMapper.toSummaryDTO(userOpt.get());
        } else {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
    }

    @Cacheable(value = "userById", key = "#id")
    public userSummaryDTO findUserById(Long id){
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if(userOpt.isPresent()){
            return userMapper.toSummaryDTO(userOpt.get());
        } else {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }

    @CacheEvict(value = {"userById", "userByEmail"}, allEntries = true)
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
        if(userDTO.getRole() != null){
            userEntity.setRole(userDTO.getRole());
        }
        UserEntity updatedUser = userRepository.save(userEntity);
        return userMapper.toSummaryDTO(updatedUser);
    }

    public Page<userSummaryDTO> getAllUsers(Pageable pageable){
      return userRepository.findAll(pageable).map(userMapper::toSummaryDTO);
    }
    
    public Page<userSummaryDTO> searchUsers(String search, Pageable pageable) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            search, search, search, pageable
        ).map(userMapper::toSummaryDTO);
    }

    public List<userSummaryDTO> getAllUsersList() {
        return userRepository.findAll().stream().map(userMapper::toSummaryDTO).toList();
    }

    @CacheEvict(value = {"userById", "userByEmail"}, allEntries = true)
    public void deleteUser(Long id){
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(userEntity);
    }
}