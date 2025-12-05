package com.bci.userapi.mapper;

import com.bci.userapi.dto.PhoneDTO;
import com.bci.userapi.dto.UserDetailResponseDTO;
import com.bci.userapi.dto.UserListResponseDTO;
import com.bci.userapi.dto.UserRequestDTO;
import com.bci.userapi.dto.UserResponseDTO;
import com.bci.userapi.dto.UserUpdateResponseDTO;
import com.bci.userapi.entity.Phone;
import com.bci.userapi.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;
    private User user;
    private UserRequestDTO userRequestDTO;
    private Phone phone;
    private PhoneDTO phoneDTO;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();

        UUID userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setName("Juan Rodriguez");
        user.setEmail("juan@rodriguez.cl");
        user.setPassword("encoded-password");
        user.setToken("test-token");
        user.setIsActive(true);
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());

        phone = new Phone();
        phone.setId(UUID.randomUUID());
        phone.setNumber("1234567");
        phone.setCitycode("1");
        phone.setCountrycode("57");
        phone.setUser(user);

        user.setPhones(new ArrayList<>());
        user.getPhones().add(phone);

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Maria Garcia");
        userRequestDTO.setEmail("maria@garcia.cl");
        userRequestDTO.setPassword("password123");

        phoneDTO = new PhoneDTO();
        phoneDTO.setNumber("7654321");
        phoneDTO.setCitycode("2");
        phoneDTO.setCountrycode("56");
        userRequestDTO.setPhones(new ArrayList<>());
        userRequestDTO.getPhones().add(phoneDTO);
    }

    @Test
    void testToEntity() {
        User result = userMapper.toEntity(userRequestDTO);

        assertNotNull(result);
        assertEquals(userRequestDTO.getName(), result.getName());
        assertEquals(userRequestDTO.getEmail(), result.getEmail());
    }

    @Test
    void testToResponseDTO() {
        UserResponseDTO result = userMapper.toResponseDTO(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getCreated(), result.getCreated());
        assertEquals(user.getModified(), result.getModified());
        assertEquals(user.getLastLogin(), result.getLastLogin());
        assertEquals(user.getToken(), result.getToken());
        assertEquals(user.getIsActive(), result.getIsactive());
    }

    @Test
    void testToDetailResponseDTO() {
        UserDetailResponseDTO result = userMapper.toDetailResponseDTO(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getIsActive(), result.getIsActive());
        assertNotNull(result.getPhones());
        assertEquals(1, result.getPhones().size());
        assertEquals(phone.getNumber(), result.getPhones().get(0).getNumber());
    }

    @Test
    void testToUpdateResponseDTO() {
        UserUpdateResponseDTO result = userMapper.toUpdateResponseDTO(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getIsActive(), result.getIsActive());
        assertEquals(user.getCreated(), result.getCreated());
        assertEquals(user.getModified(), result.getModified());
        assertNotNull(result.getPhones());
        assertEquals(1, result.getPhones().size());
    }

    @Test
    void testToListResponseDTO() {
        UserListResponseDTO result = userMapper.toListResponseDTO(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getIsActive(), result.getIsActive());
        assertEquals(user.getCreated(), result.getCreated());
        assertEquals(user.getModified(), result.getModified());
        assertEquals(user.getLastLogin(), result.getLastLogin());
        assertNotNull(result.getPhones());
        assertEquals(1, result.getPhones().size());
    }

    @Test
    void testToPhoneEntity() {
        Phone result = userMapper.toPhoneEntity(phoneDTO, user);

        assertNotNull(result);
        assertEquals(phoneDTO.getNumber(), result.getNumber());
        assertEquals(phoneDTO.getCitycode(), result.getCitycode());
        assertEquals(phoneDTO.getCountrycode(), result.getCountrycode());
        assertEquals(user, result.getUser());
    }

    @Test
    void testToPhoneDTO() {
        PhoneDTO result = userMapper.toPhoneDTO(phone);

        assertNotNull(result);
        assertEquals(phone.getNumber(), result.getNumber());
        assertEquals(phone.getCitycode(), result.getCitycode());
        assertEquals(phone.getCountrycode(), result.getCountrycode());
    }

    @Test
    void testToPhoneDTOList() {
        List<Phone> phones = new ArrayList<>();
        phones.add(phone);

        Phone phone2 = new Phone();
        phone2.setNumber("9999999");
        phone2.setCitycode("3");
        phone2.setCountrycode("55");
        phones.add(phone2);

        List<PhoneDTO> result = userMapper.toPhoneDTOList(phones);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(phone.getNumber(), result.get(0).getNumber());
        assertEquals(phone2.getNumber(), result.get(1).getNumber());
    }

    @Test
    void testToPhoneDTOList_EmptyList() {
        List<Phone> phones = new ArrayList<>();
        List<PhoneDTO> result = userMapper.toPhoneDTOList(phones);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testToDetailResponseDTO_NoPhones() {
        user.setPhones(new ArrayList<>());
        UserDetailResponseDTO result = userMapper.toDetailResponseDTO(user);

        assertNotNull(result);
        assertNotNull(result.getPhones());
        assertTrue(result.getPhones().isEmpty());
    }
}

