package com.example.commerce.interfaces;

import com.example.commerce.dtos.requests.LoginDTO;
import com.example.commerce.dtos.requests.UpdateUserDTO;
import com.example.commerce.dtos.requests.UserRegistrationDTO;
import com.example.commerce.dtos.responses.LoginResponseDTO;
import com.example.commerce.dtos.responses.userSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    LoginResponseDTO addUser(UserRegistrationDTO userDTO);

    LoginResponseDTO loginUser(LoginDTO loginDTO);

    userSummaryDTO findUserByEmail(String email);

    userSummaryDTO findUserById(Long id);

    userSummaryDTO updateUser(Long id, UpdateUserDTO userDTO);

    Page<userSummaryDTO> getAllUsers(Pageable pageable);

    List<userSummaryDTO> getAllUsersList();

    void deleteUser(Long id);
}
