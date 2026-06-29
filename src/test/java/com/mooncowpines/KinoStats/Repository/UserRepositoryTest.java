package com.mooncowpines.KinoStats.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import com.mooncowpines.KinoStats.Model.User;

@DataJpaTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;

    @Test
    void cp39_findByEmail_devuelveUsuarioConEmailExistente() {
        Optional<User> result = userRepository.findByEmail("test@kino.com");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findByEmail_devuelveEmptyConEmailInexistente() {
        Optional<User> result = userRepository.findByEmail("nada@kino.com");

        assertThat(result).isEmpty();
    }
}