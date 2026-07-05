package it.unibo.token;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import it.unibo.key.KeyManager;

@Service
public class TokenService {
    
    private static final int TIME_EXPIRATION = 10; // hours
    private static final String ZONE_ID = "+1";
    private static final String ISSUER = "auth-token";

    /**
     * Generates the token for the specified user and 
     * the Authenticator signs it with its private key.
     * @param user that requires the token.
     * @return a String representing the token.
     * @throws Exception 
     */
    public String generateToken(String user) throws Exception {
        Algorithm algorithm = Algorithm.RSA512(
            (RSAPublicKey) KeyManager.loadAuthenticatorPublicKey(), 
            (RSAPrivateKey) KeyManager.loadAuthenticatorPrivateKey());
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user)
                .withClaim("username", user)
                .withExpiresAt(getExpirationDate())
                .sign(algorithm);
    }

    /**
     * Checks the Issuer of the token.
     * @param token the token to verify.
     * @return true if it is ok (the correct token has been received) and false otherwise.
     */
    public boolean checkIssuer(String token) {
        return JWT.decode(token).getIssuer().equals(ISSUER);
    }

    /**
     * Gets the paramater from the specified claim.
     * @param token the token to verify.
     * @param claim the field to return.
     * @return a String of the desired parameter.
     */
    public String getClaimFromToken(String token, String claim) {
        return JWT.decode(token).getClaim(claim).asString();
    }

    /**
     * Checks the validity of the token and returns a decoded version of it.
     * @param token as astring.
     * @return the token decoded if the validation is positive (the signature can be checked).
     * @throws Exception
     */
    public DecodedJWT getTokenVerified(String token, RSAPublicKey publicKey) throws Exception {
        Algorithm algorithm = Algorithm.RSA512(publicKey, null);
        return JWT.require(algorithm).build().verify(token);
    }

    /**
     * Checks the expiration date of the token.
     * @param expirationDate inside the token.
     * @return true if the token is still valid and false otherwise.
     */
    public boolean checkExpirationDate(Instant expirationDate) {
        return expirationDate.compareTo(LocalDateTime.now().toInstant(ZoneOffset.of(ZONE_ID))) > 0;
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now().plusHours(TIME_EXPIRATION).toInstant(ZoneOffset.of(ZONE_ID));
    }
}
