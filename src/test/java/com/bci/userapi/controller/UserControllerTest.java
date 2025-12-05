package com.bci.userapi.controller;

import com.bci.userapi.dto.PhoneDTO;
import com.bci.userapi.dto.UserRequestDTO;
import com.bci.userapi.dto.UserResponseDTO;
import com.bci.userapi.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequestDTO userRequest;
    private UserResponseDTO userResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequestDTO();
        userRequest.setName("Juan Rodriguez");
        userRequest.setEmail("juan@rodriguez.cl");
        userRequest.setPassword("hunter2");
        
        PhoneDTO phone = new PhoneDTO();
        phone.setNumber("1234567");
        phone.setCitycode("1");
        phone.setCountrycode("57");
        userRequest.setPhones(new ArrayList<>());
        userRequest.getPhones().add(phone);

        userResponse = new UserResponseDTO();
        userResponse.setId(UUID.randomUUID());
        userResponse.setName("Juan Rodriguez");
        userResponse.setEmail("juan@rodriguez.cl");
        userResponse.setCreated(LocalDateTime.now());
        userResponse.setModified(LocalDateTime.now());
        userResponse.setLastLogin(LocalDateTime.now());
        userResponse.setToken("test-token");
        userResponse.setIsactive(true);
    }

    @Test
    void testCreateUser_Success() throws Exception {
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Juan Rodriguez"))
                .andExpect(jsonPath("$.email").value("juan@rodriguez.cl"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.isactive").value(true));
    }

    @Test
    void testCreateUser_InvalidRequest() throws Exception {
        userRequest.setName("");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
    }
}

