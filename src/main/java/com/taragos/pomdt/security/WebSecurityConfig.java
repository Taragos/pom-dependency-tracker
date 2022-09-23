package com.taragos.pomdt.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Global configuration for the security aspect of this application.
 * Defines the basic authentication used for the routes.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig  {

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
                .authorizeRequests((requests) -> requests.antMatchers("/actuator/health").permitAll())
                .authorizeRequests((requests) -> requests.antMatchers("/actuator/health/*").permitAll())
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .httpBasic();

        return http.build();
    }

    /**
     * Defines the two basic authentication credentials pairs that can be used for authentication.
     *      USER -> Basic Authentication for any real person wanting to use the UI.
     *      SYSTEM -> Basic Authentication used by automated systems to import information.
     * @return an InMemoryUserDetailsManager filled with the credential pairs
     */
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
