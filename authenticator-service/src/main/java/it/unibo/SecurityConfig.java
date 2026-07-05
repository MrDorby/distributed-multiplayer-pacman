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

    /*
     * UsernamePasswordAuthenticationFilter: 
     * Tries to find a username/password request parameter/POST body and if found, 
     * tries to authenticate the user with those values. 
    */
    @Bean
    public SecurityFilterChain securityFIlterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            //.anonymous(anonymous -> anonymous.principal("guest").authorities("ROLE_USER"))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll() 
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/syn").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/token").permitAll())
                //.anyRequest().authenticated())
            .build();
    }

}
