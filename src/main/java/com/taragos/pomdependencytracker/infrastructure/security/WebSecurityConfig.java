package com.taragos.pomdependencytracker.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Value("${spring.auth.user.username}")
    String userUsername;
    @Value("${spring.auth.user.password}")
    String userPassword;


    @Value("${spring.auth.system.username}")
    String systemUsername;
    @Value("${spring.auth.system.password}")
    String systemPassword;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .httpBasic();

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        final UserDetails user = User
                .withUsername(userUsername)
                .password("{noop}" + userPassword)
                .roles("USER")
                .build();

        final UserDetails system = User
                .withUsername(systemUsername)
                .password("{noop}" + systemPassword)
                .roles("SYSTEM")
                .build();

        return new InMemoryUserDetailsManager(user, system);
    }
}
