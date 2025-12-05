package com.bci.userapi.service;

import com.bci.userapi.dto.ChangePasswordRequestDTO;
import com.bci.userapi.dto.ChangePasswordResponseDTO;
import com.bci.userapi.dto.DeleteUserResponseDTO;
import com.bci.userapi.dto.UserDetailResponseDTO;
import com.bci.userapi.dto.UserListResponseDTO;
import com.bci.userapi.dto.UserRequestDTO;
import com.bci.userapi.dto.UserResponseDTO;
import com.bci.userapi.dto.UserUpdateRequestDTO;
import com.bci.userapi.dto.UserUpdateResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    UserResponseDTO createUser(UserRequestDTO userRequest);
    UserDetailResponseDTO getUserById(UUID id);
    List<UserListResponseDTO> getAllUsers();
    UserUpdateResponseDTO updateUser(UUID id, UserUpdateRequestDTO userRequest);
    ChangePasswordResponseDTO changePassword(UUID id, ChangePasswordRequestDTO request);
    DeleteUserResponseDTO deleteUser(UUID id);
}

