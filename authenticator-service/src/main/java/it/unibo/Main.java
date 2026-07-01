package it.unibo;

import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import it.unibo.key.KeyGenerator;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
        //String encryptedPublic = "fwSgRxImaDWKhaDXvdb8Nf2/7FukCu969z29cTlXxX698bVHpFtD6QtxCro9rvt+yhr+2PAx3yXcHAGzcfroK/QskTQAIodQvmlly+K0MCZY8bzPpqSAubFViUwklGDOZuvtg9isPXdBx5qUfRx73KmxGKLlwzHb+Wppb/j/avxF/CLYbBEgFefWtV60yVi06hW9Ob0xgjFW/F/IwZkvYzspo3OzZubaJ/HmSNzmn3saMSRDvcTiZWXxt7rrc6x5yhrdRNTBWVTJwzoS2dTqo6Zf8mJn5sp905mwkYbl9Yjwk1y541/JlUUrK6gfctAbOP218t4asz09Zq/Xm2Ayc0I6lwgXbjr7zBH4Sy5jNkOCdczGVT/tjRlV8D7qfNwR4evDK9Ow+Ev6a081SSe55MZ2Qiuq8qAUN4zOzdybyxF267SFBm7AsuCCc4zAnOvz6AM7Rh2bR1AaqobZyNHe6PHrvK2NTkPLpQ+vKM61b7q16nY0kaHJ0ABZwIZKS/kQQXi98/0aDRnIdM9RTcGPxubFJ4RHKEaxGkM86YjbK3/dlU1pCH8RmZK3hPiQIpWEXOZrfXPcFieZVTH1WMYyTqApzqcuWcNFBcUN6y+/5d72nhJkTBQ3r8LFLUeE2vPIIJOhq7EkTtR6T0oXbyiOKdhK+a+yJgwyVQW8UzB0j34="; 
        // System.out.println(encryptedPublic.length());
        //String b = "X9AsXcj3J9jrpasD4KKjZnZ8LpqpSB4qKoij/VzGRhLGTP3/oTDIIVe3kIQEBX2zNNvuIzV5hhXEs802ZSdfJ1ZaZZwi6NcULqNDJBDzlwAGWKcqc6T4mjOK2BNB//PsoVVKtAATVqsigYYcvGVShsA9sCHMWzsEW63ezq4Wmh145gDb/246PB4z7Ve8H48nzXfU8xjOUJylUk+rRdnc/BiyQLjM+hl20zlE/KYVKWIILDyRTLeU5/NKfLaA0q52XsE0qzGVCBzk7A1Zr2CAhI5cxU6qh3Rx48NWRh1a7z7PSfNXnTaLzbJpsHhn4tP6VKilij6q4X78YOFFwapmIgjpTgyQfz5LtsBCl6KKn69KAOD8Oj+RWhkzdbfUJzRhP29KNdrvRRf9i6vMEIHR/ucO1CL/hOpLRsPRZouIbu6ZKupE3XO8/BzxXxiHFzunWAfTql5OazQeVbboMLBJTQv1DSEVr0saCHKCIsQ/ModDX7o8S2skZIGMieHTVc2gqPfDKlTX0FU884nmZCLWBbCMA2Z+qXa1FeB4vQEmKf7pdpQjfIxhbjO5ZurQcFrATXeTc4why/dFWIaLtOkltIoOdCPMUVpplE+jscW74u5VlO91gjwlH533M5G4hDwiHjX6z1hMLfXI2GPxCVIHt9pB08xeBXHfpu18Ptl+dWA=";
        //System.out.println(encryptedPublic.equals(b));
        //PublicKey pk = KeyGenerator.loadAuthenticatorPublicKey();
        //System.out.println("MODULUS PUBBLICO: " + ((java.security.interfaces.RSAPublicKey) pk).getModulus().toString(16).substring(0, 20));
        //PrivateKey prk = KeyGenerator.loadAuthenticatorPrivateKey();
        //System.out.println("MODULUS Privato: " + ((java.security.interfaces.RSAPrivateKey) prk).getModulus().toString(16).substring(0, 20));
        
        //String data = "{\"username\": \"pippo\", \"password\": \"1234\"}";
        //String encrypted = KeyGenerator.encryptDecryptDataWithKey(data, Cipher.ENCRYPT_MODE, pk);
        //System.out.println(encrypted + "\n\n\n");
        // System.out.println(encryptedPublic.equals(encrypted));
        //PrivateKey prk = KeyGenerator.loadAuthenticatorPrivateKey();
        //String d = "GhKc6niot4fhQXUTc8PWEwv6BSTn7fnQdc9u35CQz6rhAVGrgt6wO1CBnrIaWr0hBFxIKzR7IjFTzJ5iClD33UMnBErQgJ0Oqihvivpn+76sZCZm+cdlGahmFR9ccG582P7flJmyBUUSHQH5F+OcuK0tJZOO5zeswd5/XJtRTuMSeCtkNjg0L0iZtPs44Kr4V6i5ReQFzohyWILZGY39Qii/jngYl8HkA6ucskrkKgDJXeQeDFbKk2RKHYqWVVDNa9G5gFEUL5vNk5EzcZ0Ju/lcIJSEis2nWnphZLXu+bdXiJLkk2AJ0drGACtiea2CLklXlTiadyOqCAXeF0Ns1dvtPdw/qpNomRH5GTuZI19cc65ooOjTfpk4uCCUSf4aHFZh9ugsQata1bQsWb+yYuaIh5hQpi9VN+V2UBdvU9B5BasIO5ojZYJucjNS/qOxG8XE9FijPljVIUcptfRyIbOuLPlZCuz9ybtR2aCWHF/eFgV/4pDTnmduLT4c1iAS2lqsDEanicKWYUl9NCG6CX3jbiFmCS3wkpqWiNWkRObx+ZrP+dgjGK79w3rOXJ4Oggj5vb5uK/IKZGASrfYiZXRKeHSVJQSb/hVUSMvlJcCPauFT100PKAtHv9WSedxolHAgzZI4haqB2P7goxr0h/wh6N9kP0DLyWUCFLprbdg=";
        //String encryptedPrivate = KeyGenerator.encryptDecryptDataWithKey(data, Cipher.ENCRYPT_MODE, prk);
        //System.out.println("\n\n\n" + encryptedPrivate);
        //System.out.println(KeyGenerator.encryptDecryptDataWithKey(encryptedPublic, Cipher.DECRYPT_MODE, prk));
        //System.out.println(KeyGenerator.encryptDecryptDataWithKey(d, Cipher.DECRYPT_MODE, prk));
        // System.out.println(pkHash);
        // System.out.println(hash);
        // System.out.println(pkHash.equals(hash));
        // System.out.println(hashByte);
        // System.out.println(hash.getBytes());
        // System.out.println(MessageDigest.isEqual(hashByte, hash.getBytes()));    
    }
}
