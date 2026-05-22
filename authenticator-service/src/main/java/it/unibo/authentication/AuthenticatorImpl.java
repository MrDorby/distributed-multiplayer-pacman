package it.unibo.authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.unibo.dto.LoginRequest;
import it.unibo.dto.LoginResponse;
import it.unibo.key.KeyGenerator;
import it.unibo.token.TokenService;

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
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
            loginRequest.username(), loginRequest.password());
        Authentication auth = this.authenticationManager.authenticate(credentials);
        String token = tokenService.generateToken((User) auth.getPrincipal()); // TODO: What does the token contain?
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @Override
    public ResponseEntity<Void> register() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'register'");
    }
    
}
