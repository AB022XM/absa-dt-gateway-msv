package ug.co.absa.gateway.security.jwt;

import static ug.co.absa.gateway.security.jwt.JwtAuthenticationTestUtils.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import ug.co.absa.gateway.IntegrationTest;

@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@AuthenticationIntegrationTest
class TokenAuthenticationIT {

    @Autowired
    private WebTestClient webTestClient;

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtKey;

    @Test
    void testLoginWithValidToken() throws Exception {
        expectOk(createValidToken(jwtKey));
    }

    @Test
    void testReturnFalseWhenJWThasInvalidSignature() throws Exception {
        expectUnauthorized(createTokenWithDifferentSignature());
    }

    @Test
    void testReturnFalseWhenJWTisMalformed() throws Exception {
        expectUnauthorized(createSignedInvalidJwt(jwtKey));
    }

    @Test
    void testReturnFalseWhenJWTisExpired() throws Exception {
        expectUnauthorized(createExpiredToken(jwtKey));
    }

    private void expectOk(String token) {
        webTestClient.get().uri("/api/authenticate").headers(headers -> headers.setBearerAuth(token)).exchange().expectStatus().isOk();
    }

    private void expectUnauthorized(String token) {
        webTestClient
            .get()
            .uri("/api/authenticate")
            .headers(headers -> headers.setBearerAuth(token))
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }
}
