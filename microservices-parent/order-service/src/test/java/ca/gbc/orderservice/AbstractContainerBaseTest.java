package ca.gbc.orderservice;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractContainerBaseTest {

    // Define the PostgreSQL container
    static final PostgreSQLContainer<?> POSTGRES_CONTAINER;

    static {
        // Specify the version of PostgreSQL you want to use
        POSTGRES_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
        POSTGRES_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
        // Set properties for Spring Data JPA
        dynamicPropertyRegistry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }
}
