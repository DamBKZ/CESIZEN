package com.cesizen.cesizen_back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false",
        "spring.mail.host=localhost",
        "spring.mail.port=25",
        "spring.mail.username=test@test.com",
        "spring.mail.password=test",
        "jwt.secret=dGVzdHNlY3JldGtleWZvcnVuaXR0ZXN0c29ubHkxMjM0NTY3",
        "jwt.expiration-ms=3600000",
        "jwt.refresh-expiration-days=7",
        "reset-password.expiration-minutes=30",
        "app.frontend-url=http://localhost:4200"
})
class CesizenBackApplicationTests {

    @Test
    void contextLoads() {
    }
}