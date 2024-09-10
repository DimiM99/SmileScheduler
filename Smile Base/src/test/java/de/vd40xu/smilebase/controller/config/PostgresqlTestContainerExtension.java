package de.vd40xu.smilebase.controller.config;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.concurrent.atomic.AtomicBoolean;

public class PostgresqlTestContainerExtension implements BeforeAllCallback, AfterAllCallback {

    private final String DockerImageVersion = "postgres:15.8";

    private final AtomicBoolean started = new AtomicBoolean(Boolean.TRUE);

    private final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageVersion);

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        if (started.getAndSet(Boolean.FALSE)) {
            postgreSQLContainer.start();
            System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
            System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
            System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        if (started.get()) {
            postgreSQLContainer.stop();
        }
    }


}