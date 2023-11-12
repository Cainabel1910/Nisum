package com.example.NisumBismark.config;

import com.example.NisumBismark.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Puede que necesites esta importación, dependiendo de cómo hayas configurado tu aplicación
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter; // Puede que necesites esta importación, dependiendo de cómo hayas configurado tu aplicación
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter; // Puede que necesites esta importación, dependiendo de cómo hayas configurado tu aplicación
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // Asegúrate de tener esta importación
import org.springframework.security.web.util.matcher.RequestMatcher; // Asegúrate de tener esta importación


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        RequestMatcher h2ConsoleMatcher = new AntPathRequestMatcher("/h2-console/**");
        RequestMatcher authMatcher = new AntPathRequestMatcher("/auth/**");

            return http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(authRequest  -> {
                                authRequest
                                        .requestMatchers(h2ConsoleMatcher, authMatcher).permitAll()
                                        .anyRequest().authenticated();
                            }
                         )
                    //.formLogin(withDefaults())
                    .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authenticationProvider(authProvider)
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
    }
}
