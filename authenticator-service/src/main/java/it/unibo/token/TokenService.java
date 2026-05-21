package it.unibo;

import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.auth0.jwt.algorithms.Algorithm;


@Service
public class TokenService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);
    private static final String PATH = "./keys/";

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.RSA512(
                (RSAPublicKey) KeyGenerator.loadPublicKey(), 
                (RSAPrivateKey) KeyGenerator.loadPrivatecKey());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public String validateToken() {
        return null;
    }
}
