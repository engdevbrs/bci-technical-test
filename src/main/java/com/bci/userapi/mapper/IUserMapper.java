package com.bci.userapi.mapper;

import com.bci.userapi.dto.PhoneDTO;
import com.bci.userapi.dto.UserDetailResponseDTO;
import com.bci.userapi.dto.UserListResponseDTO;
import com.bci.userapi.dto.UserRequestDTO;
import com.bci.userapi.dto.UserResponseDTO;
import com.bci.userapi.dto.UserUpdateResponseDTO;
import com.bci.userapi.entity.Phone;
import com.bci.userapi.entity.User;

import java.util.List;

public interface IUserMapper {
    User toEntity(UserRequestDTO dto);
    UserResponseDTO toResponseDTO(User entity);
    UserDetailResponseDTO toDetailResponseDTO(User entity);
    UserUpdateResponseDTO toUpdateResponseDTO(User entity);
    UserListResponseDTO toListResponseDTO(User entity);
    Phone toPhoneEntity(PhoneDTO dto, User user);
    PhoneDTO toPhoneDTO(Phone entity);
    List<PhoneDTO> toPhoneDTOList(List<Phone> phones);
}

