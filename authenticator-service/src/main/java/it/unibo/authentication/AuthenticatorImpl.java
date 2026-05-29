package it.unibo.authentication;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unibo.dto.LoginDTO;
import it.unibo.dto.LoginResponse;
import it.unibo.dto.RegisterDTO;
import it.unibo.key.KeyGenerator;
import it.unibo.token.TokenService;
import it.unibo.mongodb.UserMongoDB;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticatorImpl {
    
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticatorImpl(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        KeyGenerator.generateKeys();
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDTO loginRequest) {
        // TODO: decrypt with private key of the server the loginRequest.
        UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
            loginRequest.username(), loginRequest.password());
        Authentication auth = this.authenticationManager.authenticate(credentials);
        UserMongoDB user = new UserMongoDB((String) auth.getPrincipal(), (String) auth.getCredentials());
        String token = tokenService.generateToken(user); // TODO: What does the token contain?
        // TODO: response needs to be encrypted with the public key of the receiver.
        return ResponseEntity.ok(new LoginResponse(token));
    }

    // TODO: The password travels encrypted by the public key of the Auth?
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterDTO registerRequest) {
        // TODO: decrypt with private key of the server the loginRequest.
        // TODO: Need to do the check with the database.
        String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequest.password());
        UserMongoDB user = new UserMongoDB(registerRequest.email(), registerRequest.username(), encryptedPassword);
        String token = tokenService.generateToken(user); // TODO: What does the token contain?
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
