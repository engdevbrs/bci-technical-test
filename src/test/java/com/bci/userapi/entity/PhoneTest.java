package com.bci.userapi.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PhoneTest {

    private Phone phone;
    private User user;

    @BeforeEach
    void setUp() {
        phone = new Phone();
        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Juan Rodriguez");
        user.setEmail("juan@rodriguez.cl");
    }

    @Test
    void testPhoneCreation() {
        UUID id = UUID.randomUUID();

        phone.setId(id);
        phone.setNumber("1234567");
        phone.setCitycode("1");
        phone.setCountrycode("57");
        phone.setUser(user);

        assertEquals(id, phone.getId());
        assertEquals("1234567", phone.getNumber());
        assertEquals("1", phone.getCitycode());
        assertEquals("57", phone.getCountrycode());
        assertEquals(user, phone.getUser());
    }

    @Test
    void testPhoneUserRelationship() {
        phone.setUser(user);
        assertEquals(user, phone.getUser());
        assertEquals(user.getEmail(), phone.getUser().getEmail());
    }
}

