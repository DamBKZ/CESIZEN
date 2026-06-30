package com.cesizen.cesizen_back.controller;
import org.junit.jupiter.api.Disabled;

import com.cesizen.cesizen_back.entity.RefreshToken;
import com.cesizen.cesizen_back.entity.Role;
import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.repository.UserRepository;
import com.cesizen.cesizen_back.service.RefreshTokenService;
import com.cesizen.cesizen_back.service.ResetPasswordTokenService;
import com.cesizen.cesizen_back.service.UserService;
import com.cesizen.cesizen_back.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled("Tests unitaires avec mocks désactivés : remplacement prévu par tests d'intégration")
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private ResetPasswordTokenService resetPasswordTokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void login_should_set_refresh_cookie_and_return_access_token() throws Exception {
        var role = Role.builder().roleId(1).roleName("USER").build();
        var user = User.builder()
                .userId("u1")
                .email("test@test.fr")
                .pseudo("test")
                .role(role)
                .build();

        when(userService.login("test@test.fr", "testtesttest")).thenReturn(user);
        when(jwtService.generateToken(any(), any())).thenReturn("ACCESS_TOKEN_TEST");
        when(refreshTokenService.create(any())).thenReturn(RefreshToken.builder()
                .refreshTokenValue("RTV123")
                .refreshTokenCreatedDate(LocalDateTime.now())
                .refreshTokenEndDate(LocalDateTime.now().plusDays(7))
                .user(user)
                .build());

        String payload = "{\"email\":\"test@test.fr\",\"password\":\"testtesttest\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("ACCESS_TOKEN_TEST"))
                .andExpect(cookie().value("refreshToken", "RTV123"))
                .andExpect(cookie().exists("XSRF-TOKEN"));
    }

    @Test
    void refresh_without_cookie_should_return_401() throws Exception {
        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Refresh token manquant"));
    }
}
