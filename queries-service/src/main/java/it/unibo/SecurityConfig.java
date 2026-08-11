package it.unibo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines which request are authorized for the Queries.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            //.anonymous(anonymous -> anonymous.principal("guest").authorities("ROLE_USER"))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll() 
                .requestMatchers(HttpMethod.POST, "/queries/token").permitAll()
                .requestMatchers(HttpMethod.POST, "/queries/info").permitAll()
                .requestMatchers(HttpMethod.POST, "/queries/syn").permitAll())
                //.anyRequest().authenticated())
            .build();
    }

}
