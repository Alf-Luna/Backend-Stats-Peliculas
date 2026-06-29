package com.mooncowpines.KinoStats.Controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void cp07_loginExitoso_devuelve200YDatos() throws Exception {
        mockMvc.perform(get("/api/v1/auth/login")
                .with(httpBasic("testuser", "password123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@kino.com"))
            .andExpect(jsonPath("$.authorities[0].authority").value("ROLE_USER"));
    }

    @Test
    void cp08_loginPasswordIncorrecta_devuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/login")
                .with(httpBasic("testuser", "wrongpass")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void cp09_loginSinCredenciales_devuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/login"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void cp10_endpointProtegidoSinAuth_devuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/logs/all/1"))
            .andExpect(status().isUnauthorized());
    }
}