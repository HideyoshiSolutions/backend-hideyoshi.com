package br.com.hideyoshi.auth.config;

import br.com.hideyoshi.auth.entity.Provider;
import br.com.hideyoshi.auth.entity.Role;
import br.com.hideyoshi.auth.model.UserDTO;
import br.com.hideyoshi.auth.repository.UserRepository;
import br.com.hideyoshi.auth.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class DefaultUserConfig {

    @Value("${br.com.hideyoshi.defaultUser.fullName}")
    private String ADMIN_NAME;

    @Value("${br.com.hideyoshi.defaultUser.email}")
    private String ADMIN_EMAIL;

    @Value("${br.com.hideyoshi.defaultUser.username}")
    private String ADMIN_USERNAME;

    @Value("${br.com.hideyoshi.defaultUser.password}")
    private String ADMIN_PASSWORD;

    @Bean
    CommandLineRunner run(UserService userService, UserRepository userRepo) {
        return args -> {
            UserDTO defaultUser = UserDTO.builder()
                    .name(ADMIN_NAME)
                    .email(ADMIN_EMAIL)
                    .username(ADMIN_USERNAME)
                    .password(ADMIN_PASSWORD)
                    .provider(Provider.LOCAL)
                    .roles(new ArrayList<>())
                    .build();
            if (!userRepo.findByUsername(defaultUser.getUsername()).isPresent()) {
                defaultUser = userService.saveUser(defaultUser);

                userService.addRoleToUser(
                        defaultUser.getId(),
                        Role.ADMIN.getDescription()
                );
                userService.addRoleToUser(
                        defaultUser.getId(),
                        Role.USER.getDescription()
                );
            }
        };
    }

}