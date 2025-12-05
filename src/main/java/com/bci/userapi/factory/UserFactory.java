package com.bci.userapi.factory;

import com.bci.userapi.dto.PhoneDTO;
import com.bci.userapi.dto.UserRequestDTO;
import com.bci.userapi.entity.Phone;
import com.bci.userapi.entity.User;
import com.bci.userapi.mapper.IUserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserFactory {

    private final IUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserFactory(IUserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserRequestDTO userRequest, String token) {
        User user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setToken(token);
        user.setIsActive(true);
        
        LocalDateTime now = LocalDateTime.now();
        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now);

        List<Phone> phones = new ArrayList<>();
        for (PhoneDTO phoneDTO : userRequest.getPhones()) {
            Phone phone = userMapper.toPhoneEntity(phoneDTO, user);
            phones.add(phone);
        }
        user.setPhones(phones);

        return user;
    }
}

