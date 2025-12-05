package com.bci.userapi.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testUserCreation() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        user.setId(id);
        user.setName("Juan Rodriguez");
        user.setEmail("juan@rodriguez.cl");
        user.setPassword("encoded-password");
        user.setToken("test-token");
        user.setIsActive(true);
        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now);
        user.setPhones(new ArrayList<>());

        assertEquals(id, user.getId());
        assertEquals("Juan Rodriguez", user.getName());
        assertEquals("juan@rodriguez.cl", user.getEmail());
        assertEquals("encoded-password", user.getPassword());
        assertEquals("test-token", user.getToken());
        assertTrue(user.getIsActive());
        assertEquals(now, user.getCreated());
        assertEquals(now, user.getModified());
        assertEquals(now, user.getLastLogin());
        assertNotNull(user.getPhones());
    }

    @Test
    void testUserWithPhones() {
        Phone phone1 = new Phone();
        phone1.setNumber("1234567");
        phone1.setCitycode("1");
        phone1.setCountrycode("57");

        Phone phone2 = new Phone();
        phone2.setNumber("7654321");
        phone2.setCitycode("2");
        phone2.setCountrycode("56");

        user.setPhones(new ArrayList<>());
        user.getPhones().add(phone1);
        user.getPhones().add(phone2);

        assertEquals(2, user.getPhones().size());
        assertEquals("1234567", user.getPhones().get(0).getNumber());
        assertEquals("7654321", user.getPhones().get(1).getNumber());
    }

    @Test
    void testUserIsActive() {
        user.setIsActive(true);
        assertTrue(user.getIsActive());

        user.setIsActive(false);
        assertFalse(user.getIsActive());
    }
}

