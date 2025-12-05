package com.bci.userapi.controller;

import com.bci.userapi.dto.ChangePasswordRequestDTO;
import com.bci.userapi.dto.ChangePasswordResponseDTO;
import com.bci.userapi.dto.DeleteUserResponseDTO;
import com.bci.userapi.dto.ErrorResponseDTO;
import com.bci.userapi.dto.UserDetailResponseDTO;
import com.bci.userapi.dto.UserListResponseDTO;
import com.bci.userapi.dto.UserRequestDTO;
import com.bci.userapi.dto.UserResponseDTO;
import com.bci.userapi.dto.UserUpdateRequestDTO;
import com.bci.userapi.dto.UserUpdateResponseDTO;
import com.bci.userapi.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequest) {
        UserResponseDTO response = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        UserDetailResponseDTO response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserListResponseDTO> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@PathVariable UUID id, 
                                       @Valid @RequestBody UserUpdateRequestDTO userRequest) {
        UserUpdateResponseDTO response = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changePassword(@PathVariable UUID id,
                                           @Valid @RequestBody ChangePasswordRequestDTO request) {
        ChangePasswordResponseDTO response = userService.changePassword(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        DeleteUserResponseDTO response = userService.deleteUser(id);
        return ResponseEntity.ok(response);
    }
}

