package learn.DeVitoStyles.domain.security;

import learn.DeVitoStyles.models.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = "test-secret-key-that-is-at-least-32-bytes-long";

    @Test
    void generatedTokenIsValidAndCarriesUsername() {
        JwtUtil jwtUtil = new JwtUtil(SECRET);

        String token = jwtUtil.generateToken(user());

        assertTrue(jwtUtil.isTokenValid(token));
        assertEquals("jsmith", jwtUtil.extractUsername(token));
        assertEquals(4, jwtUtil.getClaims(token).get("userId", Integer.class));
        assertEquals("CUSTOMER", jwtUtil.getClaims(token).get("role", String.class));
    }

    @Test
    void tokenSignedWithDifferentSecretIsRejected() {
        String token = new JwtUtil(SECRET).generateToken(user());

        JwtUtil otherKey = new JwtUtil("a-completely-different-secret-key-of-32-plus-bytes");

        assertFalse(otherKey.isTokenValid(token));
    }

    @Test
    void garbageTokenIsRejected() {
        assertFalse(new JwtUtil(SECRET).isTokenValid("not-a-jwt"));
    }

    @Test
    void missingSecretFailsFast() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> new JwtUtil(""));
        assertTrue(ex.getMessage().contains("JWT_SECRET"));
    }

    @Test
    void shortSecretFailsFast() {
        assertThrows(IllegalStateException.class, () -> new JwtUtil("too-short"));
    }

    private User user() {
        User user = new User();
        user.setUserId(4);
        user.setUsername("jsmith");
        user.setRole("CUSTOMER");
        return user;
    }
}
