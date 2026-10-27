package learn.DeVitoStyles.domain.security;

import learn.DeVitoStyles.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

class JwtFilterTest {

    private static final String SECRET = "test-secret-that-is-at-least-32-bytes-long!!";

    private JwtUtil jwtUtil;
    private JwtFilter filter;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
        filter = new JwtFilter(jwtUtil);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Authentication filterWith(String authorization) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (authorization != null) {
            request.addHeader("Authorization", authorization);
        }
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private String tokenFor(String username, String role) {
        User user = new User();
        user.setUserId(1);
        user.setUsername(username);
        user.setRole(role);
        return jwtUtil.generateToken(user);
    }

    @Test
    void adminTokenGetsAdminRole() throws Exception {
        Authentication auth = filterWith("Bearer " + tokenFor("mstone1", "ADMIN"));

        assertThat(auth.getName()).isEqualTo("mstone1");
        assertThat(auth.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_ADMIN");
    }

    @Test
    void customerTokenGetsCustomerRole() throws Exception {
        Authentication auth = filterWith("Bearer " + tokenFor("jdoe", "CUSTOMER"));

        assertThat(auth.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_CUSTOMER");
    }

    @Test
    void missingOrInvalidTokenLeavesRequestAnonymous() throws Exception {
        assertThat(filterWith(null)).isNull();
        assertThat(filterWith("Bearer not-a-real-token")).isNull();
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() throws Exception {
        JwtUtil otherKey = new JwtUtil("a-completely-different-secret-of-32-bytes!!");
        User user = new User();
        user.setUserId(1);
        user.setUsername("mallory");
        user.setRole("ADMIN");

        assertThat(filterWith("Bearer " + otherKey.generateToken(user))).isNull();
    }
}
