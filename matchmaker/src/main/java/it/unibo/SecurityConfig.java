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
     * Defines which request are authorized for the Matchmaker.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            //.anonymous(anonymous -> anonymous.principal("guest").authorities("ROLE_USER"))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll() 
                .requestMatchers(HttpMethod.POST, "/matchmaker/join_lobby").permitAll()
                .requestMatchers(HttpMethod.POST, "/matchmaker/quit_lobby").permitAll()
                .requestMatchers(HttpMethod.POST, "/matchmaker/game_server").permitAll()
                .requestMatchers(HttpMethod.POST, "/matchmaker/delete_match").permitAll()
                .requestMatchers(HttpMethod.POST, "/matchmaker/quit_match").permitAll())
                //.anyRequest().authenticated())
            .build();
    }

}
