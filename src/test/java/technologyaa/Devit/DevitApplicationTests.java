package technologyaa.Devit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"spring.datasource.url=jdbc:h2:mem:testdb",
	"spring.datasource.username=sa",
	"spring.datasource.password=",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
	"spring.jpa.show-sql=false",
	"spring.jwt.secret=test-secret-key-for-testing-purposes-only-minimum-length-required",
	"spring.jwt.access-token-expiration=3600000",
	"spring.jwt.refresh-token-expiration=86400000",
	"spring.data.redis.host=localhost",
	"spring.data.redis.port=6379",
	"spring.security.oauth2.client.registration.google.client-id=test-client-id",
	"spring.security.oauth2.client.registration.google.client-secret=test-client-secret",
	"server.port=0"
})
class DevitApplicationTests {

	@Test
	void contextLoads() {
	}

}
