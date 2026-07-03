package it.unibo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /*UsernamePasswordAuthenticationFilter: 
    Tries to find a username/password request parameter/POST body and if found, 
    tries to authenticate the user with those values. 
    */
    @Bean
    public SecurityFilterChain securityFIlterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            //.anonymous(anonymous -> anonymous.principal("guest").authorities("ROLE_USER"))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            //.addFilter(new UsernamePasswordAuthenticationFilter())
            //.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // TODO here we need to add the error/ accessible by anyone.
                .requestMatchers("/error").permitAll() 
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/syn").permitAll())
                //.anyRequest().authenticated())
            //.formLogin(null)
            .build();
    }

    // @Bean
    // public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
    //     return authenticationConfiguration.getAuthenticationManager();
    // }

    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    //     //return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    // }

}
