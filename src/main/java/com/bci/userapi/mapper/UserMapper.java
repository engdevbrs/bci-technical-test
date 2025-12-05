package com.bci.userapi.mapper;

import com.bci.userapi.dto.PhoneDTO;
import com.bci.userapi.dto.UserDetailResponseDTO;
import com.bci.userapi.dto.UserListResponseDTO;
import com.bci.userapi.dto.UserRequestDTO;
import com.bci.userapi.dto.UserResponseDTO;
import com.bci.userapi.dto.UserUpdateResponseDTO;
import com.bci.userapi.entity.Phone;
import com.bci.userapi.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper implements IUserMapper {

    @Override
    public User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    @Override
    public UserResponseDTO toResponseDTO(User entity) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(entity.getId());
        dto.setCreated(entity.getCreated());
        dto.setModified(entity.getModified());
        dto.setLastLogin(entity.getLastLogin());
        dto.setToken(entity.getToken());
        dto.setIsactive(entity.getIsActive());
        return dto;
    }

    @Override
    public UserDetailResponseDTO toDetailResponseDTO(User entity) {
        UserDetailResponseDTO dto = new UserDetailResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setPhones(toPhoneDTOList(entity.getPhones()));
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    @Override
    public UserUpdateResponseDTO toUpdateResponseDTO(User entity) {
        UserUpdateResponseDTO dto = new UserUpdateResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setPhones(toPhoneDTOList(entity.getPhones()));
        dto.setIsActive(entity.getIsActive());
        dto.setCreated(entity.getCreated());
        dto.setModified(entity.getModified());
        return dto;
    }

    @Override
    public UserListResponseDTO toListResponseDTO(User entity) {
        UserListResponseDTO dto = new UserListResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setPhones(toPhoneDTOList(entity.getPhones()));
        dto.setCreated(entity.getCreated());
        dto.setModified(entity.getModified());
        dto.setIsActive(entity.getIsActive());
        dto.setLastLogin(entity.getLastLogin());
        return dto;
    }

    @Override
    public Phone toPhoneEntity(PhoneDTO dto, User user) {
        Phone phone = new Phone();
        phone.setNumber(dto.getNumber());
        phone.setCitycode(dto.getCitycode());
        phone.setCountrycode(dto.getCountrycode());
        phone.setUser(user);
        return phone;
    }

    @Override
    public PhoneDTO toPhoneDTO(Phone entity) {
        PhoneDTO dto = new PhoneDTO();
        dto.setNumber(entity.getNumber());
        dto.setCitycode(entity.getCitycode());
        dto.setCountrycode(entity.getCountrycode());
        return dto;
    }

    @Override
    public List<PhoneDTO> toPhoneDTOList(List<Phone> phones) {
        return phones.stream()
                .map(this::toPhoneDTO)
                .collect(Collectors.toList());
    }
}

