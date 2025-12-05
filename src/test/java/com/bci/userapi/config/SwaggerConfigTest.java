package com.bci.userapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springfox.documentation.spring.web.plugins.Docket;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SwaggerConfigTest {

    @Autowired
    private SwaggerConfig swaggerConfig;

    @Autowired
    private Docket docket;

    @Test
    void testSwaggerConfigExists() {
        assertNotNull(swaggerConfig);
    }

    @Test
    void testDocketBean() {
        assertNotNull(docket);
    }
}

