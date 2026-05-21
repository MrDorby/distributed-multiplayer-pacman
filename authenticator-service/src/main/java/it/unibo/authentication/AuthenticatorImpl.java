package it.unibo.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.authentication.AuthenticationManagerFactoryBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.unibo.KeyGenerator;
import it.unibo.LoginRequest;

@RestController
public class AuthenticatorImpl implements Authenticator{
    
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticatorImpl(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        KeyGenerator.generateKeys();
    }

    @Override
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        Authentication auth = this.authenticationManager.authenticate(credentials);
        var token = tokenService;
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> register() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'register'");
    }
    
}
