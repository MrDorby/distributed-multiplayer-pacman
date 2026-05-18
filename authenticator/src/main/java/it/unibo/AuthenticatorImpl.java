package it.unibo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.authentication.AuthenticationManagerFactoryBean;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticatorImpl implements Authenticator{
    
    //private final AuthenticationManager authenticationManager;
    //private final TokenService tokenService;

    public AuthenticatorImpl() {
        //this.authenticationManager
    }

    @Override
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        var credentials = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        return null;
    }

    @Override
    public ResponseEntity<Void> register() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'register'");
    }
    
}
