package com.bci.userapi.factory;

import com.bci.userapi.dto.PhoneDTO;
import com.bci.userapi.dto.UserRequestDTO;
import com.bci.userapi.entity.Phone;
import com.bci.userapi.entity.User;
import com.bci.userapi.mapper.IUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFactoryTest {

    @Mock
    private IUserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserFactory userFactory;
    private UserRequestDTO userRequestDTO;
    private User user;
    private Phone phone;

    @BeforeEach
    void setUp() {
        userFactory = new UserFactory(userMapper, passwordEncoder);

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Juan Rodriguez");
        userRequestDTO.setEmail("juan@rodriguez.cl");
        userRequestDTO.setPassword("hunter123");

        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setNumber("1234567");
        phoneDTO.setCitycode("1");
        phoneDTO.setCountrycode("57");
        userRequestDTO.setPhones(new ArrayList<>());
        userRequestDTO.getPhones().add(phoneDTO);

        user = new User();
        user.setName("Juan Rodriguez");
        user.setEmail("juan@rodriguez.cl");

        phone = new Phone();
        phone.setNumber("1234567");
        phone.setCitycode("1");
        phone.setCountrycode("57");

        when(userMapper.toEntity(userRequestDTO)).thenReturn(user);
        when(passwordEncoder.encode("hunter123")).thenReturn("encoded-password");
        lenient().when(userMapper.toPhoneEntity(any(PhoneDTO.class), any(User.class))).thenReturn(phone);
    }

    @Test
    void testCreateUser() {
        String token = "test-token-123";

        User result = userFactory.createUser(userRequestDTO, token);

        assertNotNull(result);
        assertEquals(userRequestDTO.getName(), result.getName());
        assertEquals(userRequestDTO.getEmail(), result.getEmail());
        assertEquals("encoded-password", result.getPassword());
        assertEquals(token, result.getToken());
        assertTrue(result.getIsActive());
        assertNotNull(result.getCreated());
        assertNotNull(result.getModified());
        assertNotNull(result.getLastLogin());
        assertEquals(result.getCreated(), result.getModified());
        assertEquals(result.getCreated(), result.getLastLogin());
        assertNotNull(result.getPhones());
        assertEquals(1, result.getPhones().size());
        assertEquals(phone, result.getPhones().get(0));

        verify(userMapper, times(1)).toEntity(userRequestDTO);
        verify(passwordEncoder, times(1)).encode("hunter123");
        verify(userMapper, times(1)).toPhoneEntity(any(PhoneDTO.class), any(User.class));
    }

    @Test
    void testCreateUser_MultiplePhones() {
        PhoneDTO phoneDTO2 = new PhoneDTO();
        phoneDTO2.setNumber("7654321");
        phoneDTO2.setCitycode("2");
        phoneDTO2.setCountrycode("56");
        userRequestDTO.getPhones().add(phoneDTO2);

        Phone phone2 = new Phone();
        phone2.setNumber("7654321");
        phone2.setCitycode("2");
        phone2.setCountrycode("56");

        when(userMapper.toPhoneEntity(phoneDTO2, user)).thenReturn(phone2);

        User result = userFactory.createUser(userRequestDTO, "test-token");

        assertNotNull(result);
        assertEquals(2, result.getPhones().size());
        verify(userMapper, times(2)).toPhoneEntity(any(PhoneDTO.class), any(User.class));
    }

    @Test
    void testCreateUser_NoPhones() {
        userRequestDTO.setPhones(new ArrayList<>());

        User result = userFactory.createUser(userRequestDTO, "test-token");

        assertNotNull(result);
        assertNotNull(result.getPhones());
        assertTrue(result.getPhones().isEmpty());
        verify(userMapper, never()).toPhoneEntity(any(PhoneDTO.class), any(User.class));
    }
}

