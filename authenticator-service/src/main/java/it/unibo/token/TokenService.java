package it.unibo.token;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import it.unibo.key.KeyGenerator;

@Service
public class TokenService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);
    private static final int TIME_EXPIRATION = 10; // hours
    private static final String ZONE_ID = "+1";

    public String generateToken(String user) {
        try {
            Algorithm algorithm = Algorithm.RSA512(
                (RSAPublicKey) KeyGenerator.loadAuthenticatorPublicKey(), 
                (RSAPrivateKey) KeyGenerator.loadAuthenticatorPrivateKey());
            return JWT.create()
                    .withIssuer("auth-token")   //TODO: how is composed the token?
                    .withSubject(user)
                    .withClaim("Username", user)
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return "";
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now().plusHours(TIME_EXPIRATION).toInstant(ZoneOffset.of(ZONE_ID));
    }
}
