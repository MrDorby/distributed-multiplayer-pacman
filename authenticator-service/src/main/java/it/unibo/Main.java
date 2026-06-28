package it.unibo;

import java.io.IOException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws IllegalArgumentException, InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        SpringApplication.run(Main.class, args);
        // String token = new TokenService().generateToken("io");
        // System.out.println(token);
        // System.out.println("\n\n\n\n");
        
        //System.out.println(JWT.require(Algorithm.RSA512((RSAPublicKey) KeyGenerator.loadAuthenticatorPublicKey(), null)).build().verify(token).getClaims());
        //System.out.println(Base64.getUrlEncoder().encodeToString(token.getBytes()));
    }
}
