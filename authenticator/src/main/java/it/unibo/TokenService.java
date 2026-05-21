package it.unibo;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.auth0.jwt.algorithms.Algorithm;


@Service
public class TokenService {
    
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC512("null"); // TODO: RSA ?
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public String validateToken() {
        return null;
    }
}
