package com.hideyoshi.backendportfolio.base.user.repo;

import com.hideyoshi.backendportfolio.base.user.entity.Provider;
import com.hideyoshi.backendportfolio.base.user.entity.Role;
import com.hideyoshi.backendportfolio.base.user.entity.User;
import com.hideyoshi.backendportfolio.base.user.model.UserDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository underTest;

    @Test
    void savesUserToDataBase() {
        // Given
        User user = this.createEntity();
        // When
        User userSaved = this.underTest.save(user);
        log.info(userSaved.getUsername());
        // Then
        assertThat(userSaved).isNotNull();
        assertThat(userSaved).isEqualTo(user);
    }

    @Test
    void canFindsUserByUsername() {
        // Given
        User userSaved = this.entityManager.persist(this.createEntity());
        this.underTest.findAll();
        // When
        Optional<User> userOnDB =
                this.underTest.findByUsername(userSaved.getUsername());
        // Then
        assertThat(userOnDB).isNotEmpty();
        assertThat(userOnDB).hasValue(userSaved);
    }

    @Test
    void cannotFindUserByUsername() {
        // When
        Optional<User> userOnDB = this.underTest.findByUsername("Batman");
        // Then
        assertThat(userOnDB).isEmpty();
    }

    private User createEntity() {
        return UserDTO.builder()
                .name("Clark Kent")
                .email("superman@gmail.com")
                .username("Superman")
                .password("password")
                .provider(Provider.LOCAL)
                .roles(List.of(Role.USER))
                .build().toEntity();
    }

}