package it.unibo.token;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

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
                    .withIssuer("auth-token")
                    .withSubject(user)
                    .withClaim("Username", user)
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return "";
    }

    /**
     * Checks the validity of the token and returns a decoded version of it.
     * @param token as astring.
     * @return the token decoded if the validation is positive (the signature can be checked) otherwise null.
     */
    public DecodedJWT getTokenVerified(String token, RSAPublicKey publicKey) {
        Algorithm algorithm;
        try {
            algorithm = Algorithm.RSA512(publicKey, null);
            return JWT.require(algorithm).build().verify(token);
        } catch (IllegalArgumentException | JWTVerificationException e) {
            LOGGER.error(e.getMessage());
            return null;
        }

        // try {
        //     Algorithm algorithm = Algorithm.RSA512(
        //         (RSAPublicKey) KeyGenerator.loadAuthenticatorPublicKey(), 
        //         null);
        //     JWTVerifier verifier = JWT.require(algorithm)
        //     // specify any specific claim validations
        //     .withIssuer("auth-token")
        //     // reusable verifier instance
        //     .build();
        //     DecodedJWT decodedJWT = verifier.verify(token);
        //     System.out.println(decodedJWT.getClaim("Username").asString());
        // } catch (JWTVerificationException | IllegalArgumentException | InvalidKeySpecException | NoSuchAlgorithmException | IOException exception) {
            
        // }
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now().plusHours(TIME_EXPIRATION).toInstant(ZoneOffset.of(ZONE_ID));
    }
}
