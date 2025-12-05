package com.bci.userapi.controller;

import com.bci.userapi.dto.ChangePasswordRequestDTO;
import com.bci.userapi.dto.ChangePasswordResponseDTO;
import com.bci.userapi.dto.PhoneDTO;
import com.bci.userapi.dto.UserDetailResponseDTO;
import com.bci.userapi.dto.UserListResponseDTO;
import com.bci.userapi.dto.UserRequestDTO;
import com.bci.userapi.dto.UserResponseDTO;
import com.bci.userapi.dto.UserUpdateRequestDTO;
import com.bci.userapi.dto.UserUpdateResponseDTO;
import com.bci.userapi.exception.UserNotFoundException;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

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
        userResponse.setId(userId);
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
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.isactive").value(true))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.modified").exists())
                .andExpect(jsonPath("$.last_login").exists());
    }

    @Test
    void testCreateUser_InvalidRequest() throws Exception {
        userRequest.setName("");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserById_Success() throws Exception {
        UserDetailResponseDTO detailResponse = new UserDetailResponseDTO();
        detailResponse.setId(userId);
        detailResponse.setName("Juan Rodriguez");
        detailResponse.setEmail("juan@rodriguez.cl");
        detailResponse.setIsActive(true);

        when(userService.getUserById(userId)).thenReturn(detailResponse);

        mockMvc.perform(get("/api/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Juan Rodriguez"))
                .andExpect(jsonPath("$.email").value("juan@rodriguez.cl"))
                .andExpect(jsonPath("$.is_active").value(true));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(userId)).thenThrow(new UserNotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/api/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado"));
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        UserListResponseDTO user1 = new UserListResponseDTO();
        user1.setId(userId);
        user1.setName("Juan Rodriguez");
        user1.setEmail("juan@rodriguez.cl");

        UUID userId2 = UUID.randomUUID();
        UserListResponseDTO user2 = new UserListResponseDTO();
        user2.setId(userId2);
        user2.setName("Maria Garcia");
        user2.setEmail("maria@garcia.cl");

        List<UserListResponseDTO> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Juan Rodriguez"))
                .andExpect(jsonPath("$[1].name").value("Maria Garcia"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
        updateRequest.setName("Juan Rodriguez Actualizado");
        updateRequest.setEmail("juan.actualizado@rodriguez.cl");

        UserUpdateResponseDTO updateResponse = new UserUpdateResponseDTO();
        updateResponse.setId(userId);
        updateResponse.setName("Juan Rodriguez Actualizado");
        updateResponse.setEmail("juan.actualizado@rodriguez.cl");

        when(userService.updateUser(userId, updateRequest)).thenReturn(updateResponse);

        mockMvc.perform(put("/api/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Juan Rodriguez Actualizado"))
                .andExpect(jsonPath("$.email").value("juan.actualizado@rodriguez.cl"));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
        updateRequest.setName("Juan Rodriguez Actualizado");
        updateRequest.setEmail("juan.actualizado@rodriguez.cl");

        when(userService.updateUser(userId, updateRequest))
                .thenThrow(new UserNotFoundException("Usuario no encontrado"));

        mockMvc.perform(put("/api/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado"));
    }

    @Test
    void testChangePassword_Success() throws Exception {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setPassword("newpassword123");
        request.setConfirmPassword("newpassword123");

        ChangePasswordResponseDTO response = new ChangePasswordResponseDTO("Contraseña cambiada con éxito");
        when(userService.changePassword(userId, request)).thenReturn(response);

        mockMvc.perform(put("/api/users/" + userId + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Contraseña cambiada con éxito"));
    }

    @Test
    void testChangePassword_NotFound() throws Exception {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setPassword("newpassword123");
        request.setConfirmPassword("newpassword123");

        when(userService.changePassword(userId, request))
                .thenThrow(new UserNotFoundException("Usuario no encontrado"));

        mockMvc.perform(put("/api/users/" + userId + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado"));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        doThrow(new UserNotFoundException("Usuario no encontrado"))
                .when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado"));
    }
}

