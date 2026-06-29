package com.mooncowpines.KinoStats.Controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mooncowpines.KinoStats.Model.User;
import com.mooncowpines.KinoStats.Repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;

    @Test
    void cp04_usernameMuyCorto_devuelve400() throws Exception {
        String body = """
            {"username":"ab","email":"a@b.com","password":"secret123"}
            """;

        mockMvc.perform(post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void cp05_passwordMuyCorto_devuelve400() throws Exception {
        String body = """
            {"username":"valid","email":"v@k.com","password":"123"}
            """;

        mockMvc.perform(post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void cp06_emailInvalido_devuelve400() throws Exception {
        String body = """
            {"username":"valid","email":"no-es-email","password":"secret123"}
            """;

        mockMvc.perform(post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext
    void cp38_passwordSeAlmacenaConHashBCrypt() throws Exception {
        String body = """
            {"username":"hashtest","email":"hash@kino.com","password":"mySecret123"}
            """;

        mockMvc.perform(post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());

        Optional<User> saved = userRepository.findByUsername("hashtest");
        assertThat(saved).isPresent();
        assertThat(saved.get().getPassword()).startsWith("$2a$");
        assertThat(saved.get().getPassword()).doesNotContain("mySecret123");
    }
}