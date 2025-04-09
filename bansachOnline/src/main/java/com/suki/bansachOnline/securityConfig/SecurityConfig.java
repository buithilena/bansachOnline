package com.suki.bansachOnline.securityConfig;


import com.suki.bansachOnline.model.User;
import com.suki.bansachOnline.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/dangkyaccount", "/register", "/save-profile", "/update-price", "/api/quanly/**", "/products", "/inventory-by-product/{productId}", "/","/giohang",
                                "/image/**", "/css/**", "/js/**", "/logonewT.png", "/sanpham", "/product-images/**", "/cart/**", "/products-by-doituong", "/quanly", "/api/login").permitAll() // Thêm /api/login
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/quanly/**", "/save-profile", "/products", "/api/login") // Bỏ CSRF cho /api/login
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService())
                        )
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            Map<String, Object> attributes = oAuth2User.getAttributes();
                            String providerId = attributes.get("sub") != null ? attributes.get("sub").toString() : attributes.get("id").toString();
                            String name = (String) attributes.get("name");
                            String picture = null;
                            String email = (String) attributes.get("email");
                            String provider = request.getRequestURI().contains("google") ? "google" : "facebook";

                            Object pictureObj = attributes.get("picture");
                            if (pictureObj instanceof Map) {
                                Map<String, Object> pictureMap = (Map<String, Object>) pictureObj;
                                Map<String, Object> data = (Map<String, Object>) pictureMap.get("data");
                                if (data != null) {
                                    picture = (String) data.get("url");
                                }
                            } else if (pictureObj instanceof String) {
                                picture = (String) pictureObj;
                            }

                            User user = userService.saveOrUpdateUser(providerId, name, picture, email, provider);
                            String token = jwtUtil.generateToken(user);
                            response.addHeader("Authorization", "Bearer " + token);

                            if (!userService.isProfileComplete(user)) {
                                response.sendRedirect("/dangky?providerId=" + providerId + "&provider=" + provider);
                            } else {
                                if ("ADMIN".equals(user.getRole())) {
                                    response.sendRedirect("/quanly");
                                } else {
                                    response.sendRedirect("/");
                                }
                            }
                        })
                )
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return userRequest -> {
            DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);
            return oAuth2User;
        };
    }
}