package it.unibo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableMongoRepositories(basePackages = "it.unibo.mongodb")
@SpringBootApplication
public class Main {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
        // String publicKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAp7N9ePIr20oE+YbmtE+qkDo6QXt0jKrmUai2BAqFcxIznLgr7XIIekSFsnTcmStO1MHYU6MZeWIBwo3H4IbI8db6hYJflHZ1gl4JchHczpxf+Xa83nZEGkQlsT8cYHf8hDCAL5ey6MoDZbXx0qHLxdglpeuBD/+ei/dKHNg3JqGB3qQ+3FHRhq+7S9Cmw6ruBFmHW6YqbsKNjzsHryyPaIAjHxvLhc/mVks55gESV9GroieOueb7snCj3+sLcXVubzom1+7wVe2DZpsG4W9rQS2va+pyVPLqJ0Sn6izGDE1mfn0G4eXocFn+2asSx5Ds/C2JKk/Wx74sYX+JOcAMPiKooXo84y5l1agXpqA15dT1wJ3bZZ0dMnCoDBr6GEzxPV+C7bMDJ0OHDzfCIKGrLcLIsDEBotFh8d87Fpomz5B7kD7XrRtMJKQZW+Pi8XdLEUEl1CHMBVLu4VdG36OSBVlV8oD8zRQavtHPyALHoZ9ixsqhoUKx8RxK19mbOwOpqUTX7VRDo3+/qMb2VNh2cq6I3ZGO66H016fh3RtFR4FWm/QbDZqyzOPutLJJmr3l5+zVyT6ubq/kbuTkFpYENhep8RvT3ZKn7jdy/gxUKQxpv5wsEFLAmR+B8LYsi8g2MgwvO9QV9mAkrf8H7NZoT8C6Bjz/MIz2fDPQDDXQ6u8CAwEAAQ==";
        // String privateKey = "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";
        
        // String input = "{\"secretKey\":\"IuVaaAIvOGJOuTy3lqoXijKQbfxBgSPtT53EL6Xq0n9uAAxaGPrULfGmACXWt2CtcDlnM01Cjy5+yjBZmJl3zzq4EtG1lk/yA1hJIOdW80MEvTZivcG5nCdbUptPv5V6G1mpoDQRtiweIAxdxTCg0efUNxWZIYYAly1+yBbDGGpIhawdnKVH3Ul2mvRHw7NTXy/KNLv6Vg8X4GdnYOf1UGJhZLbhQ5PClX1hXVY1Pu6BQWYuzW18pba5/p3iwOn2sFoEwCgnlauL00BCCgm7WAXo+FLbEGgxalHIIGlhlmXWnOL1rqyt3UwfDl/kDRlh51sfOpmTojtIok+c1OiefxNBdDBmGShhbZItZko+V0RVgV3MAUSf2EsX4LM26zsy8pCxqh+2XP1vwaww/0b5R62sWlXUeMgiCsANP/uYJeaNZv4XvSLc/NWhM1/NmjTftuengfr+vQWAcmVMAimz8SnAIo9AKU7ZLtrpB8DjE5AcUGv2plZjG4M2Ys+NKdKZ3b/LGFlGnQJNCUAJPuMCCqtVHZBL4M5FoxBxTsqIVAjb93OKoyhUJOnQpTQLq80REG+a6XV8RdCFPuWGQPXXMGydkfQ5j154ihlxY42a2mC8z7juEJEzQ+3+8q2GsIQqdam0f4LhZCWMtRNtjL5UGJm+iPAHtYBENgRB+9rIUS4=\",\"encryptedToken\":\"LhCL4/Fo8FpCs/G/P5ki2hd/EDCfxYyqJ6goUxz1+7rbUtbjwusS3XcDuvT6hWECGghfj9j32FoV7E9j6fDt7hLh27c+kfk1EQv7wN/+5XKgX0MntJ/g7RiVCQKLFXBSinM45XPCJrlWlxCX5sMB/OBmzVHnHANS0fl9nSFyIoWdYhi1TyvCf+oQ8fQTVL+m6o4fh3M5pExrY8ZTbYRKMtd7lFQDTZTd/pUrxaj/hSA0a3q988Tvn7rFDYS2Q2i25qHu4DE4tpvzaQV9u7/mk+bLY/OfNI2taQ75Y3wrodSTzJbZ9W9NS0561i7yNghtauX3qWHa6VXy8iX3+azzZOZf0BVYM07Yv6M2Ej8kBi3N9w9at7QSp7+w94FxDyPqmJJHnxI/RNVdPHlRaLdN6DCpIGyD9xmdZSAFlWDojamTi2x/ydp2/onsc43RuldJTzGCnBOBqmwiNY+BECtQuYwcuLs17KtEhYr0PZMECgOovEm2uvbO4pVx6ZXG/K2LP19EJeih77jjdEI672abF3znsSAgT90M0gaDtxYPXJ/YNuj3Mzp55Rfsa9f4OSnoL6f56skhaTJyADVBIxf7jPHfIYE/4vBmdrOu3Rm+rCabgKfVwtpS644WezHyDytiAix5aBDWxduRYVUAigK0xo4ronOeTpfbBJCMVv4d3ozhX2XS/ovS5Bkut0QwmPslF1b8n4aWrGLIUUOJxaQv3MvsI81HgHikA2IseA71oH8PHCF1qOYmi+IigYbTu6o7acPzjUJ4laAfFt9O/NZLm7IofvbuCUmehOXb0FR8Thd+uNXylg7QsbFUldKj+VFZMAqe2myff/nRWi0cfoC+72HeDRXJeKiJcmCNwRqhnGN3h/xClJXsDrE5fHYP5S29hJYiuC++UpI8/55jqkcXH03Oh89bpXYR33yKBJiVWeTVdOici+TfQMWXqzJ3XtU/cd7cMT5aVefE03Un4UNKup3uBo7An+umy9GMOIXAlSGbEPUlxKZIFRqHq6qsftpNs7O/F2aELSDCOhcQzHSKbOv9rkAu+TUHkIc2ZZrRKdEwNDvWrqgx8+aQswL47za6kK0HIT4jzzBYOVD9xI84dw==\",\"ivParameter\":\"AAAAAAAAAAAAAAAAAAAAAA==\"}";
        // ObjectMapper mapper = new ObjectMapper();
        // EncryptedTokenDTO encryptedTokenDTO = mapper.readValue(input, EncryptedTokenDTO.class);
        // String secretKey = KeyManager.encryptDecryptDataRSA(encryptedTokenDTO.secretKey(), Cipher.DECRYPT_MODE, KeyManager.getPrivateKeyFromString(privateKey));
        // IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(encryptedTokenDTO.ivParameter()));
        // SecretKey sk = KeyManager.getSecretKeyFromString(secretKey);
        // Cipher cipher = Cipher.getInstance(AES_INSTANCE);
        // cipher.init(Cipher.DECRYPT_MODE, sk, ivParameterSpec);
        // byte[] encryptedDataBytes = Base64.getMimeDecoder().decode(encryptedTokenDTO.encryptedToken());
        // byte[] output = cipher.doFinal(encryptedDataBytes);
        // String tk = new String(output);
        
        // TokenDTO tokenDTO = mapper.readValue(tk, TokenDTO.class);
        // //String token = KeyManager.encryptDecryptData(encryptedToken, Cipher.DECRYPT_MODE, sk);
        // System.out.println(tokenDTO.token());
        
        // System.out.println(JWT.decode(tokenDTO.token()).getToken());

        // String pk = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAp7N9ePIr20oE+YbmtE+qkDo6QXt0jKrmUai2BAqFcxIznLgr7XIIekSFsnTcmStO1MHYU6MZeWIBwo3H4IbI8db6hYJflHZ1gl4JchHczpxf+Xa83nZEGkQlsT8cYHf8hDCAL5ey6MoDZbXx0qHLxdglpeuBD/+ei/dKHNg3JqGB3qQ+3FHRhq+7S9Cmw6ruBFmHW6YqbsKNjzsHryyPaIAjHxvLhc/mVks55gESV9GroieOueb7snCj3+sLcXVubzom1+7wVe2DZpsG4W9rQS2va+pyVPLqJ0Sn6izGDE1mfn0G4eXocFn+2asSx5Ds/C2JKk/Wx74sYX+JOcAMPiKooXo84y5l1agXpqA15dT1wJ3bZZ0dMnCoDBr6GEzxPV+C7bMDJ0OHDzfCIKGrLcLIsDEBotFh8d87Fpomz5B7kD7XrRtMJKQZW+Pi8XdLEUEl1CHMBVLu4VdG36OSBVlV8oD8zRQavtHPyALHoZ9ixsqhoUKx8RxK19mbOwOpqUTX7VRDo3+/qMb2VNh2cq6I3ZGO66H016fh3RtFR4FWm/QbDZqyzOPutLJJmr3l5+zVyT6ubq/kbuTkFpYENhep8RvT3ZKn7jdy/gxUKQxpv5wsEFLAmR+B8LYsi8g2MgwvO9QV9mAkrf8H7NZoT8C6Bjz/MIz2fDPQDDXQ6u8CAwEAAQ==";
        // System.out.println(Hash.hashing(pk.getBytes(), "SHA-256"));
        //SecretKey k = KeyGenerator.randomSecretKey();
        //System.out.println(k instanceof Key);
        //System.out.println(Cipher.getInstance("AES/CBC/PKCS5Padding").getAlgorithm());
        // TokenService tokenService = new TokenService();
        // String token = tokenService.generateToken("pippo");
        // System.out.println(token);
        // String json = new ObjectMapper().writeValueAsString(new TokenDTO(token));
        // System.out.println("\n\n\n" + json + "\n");
        // TokenDTO tokenDTO = new ObjectMapper().readValue(json, TokenDTO.class); 
        // System.out.println(tokenDTO);
        //String encryptedPublic = "fwSgRxImaDWKhaDXvdb8Nf2/7FukCu969z29cTlXxX698bVHpFtD6QtxCro9rvt+yhr+2PAx3yXcHAGzcfroK/QskTQAIodQvmlly+K0MCZY8bzPpqSAubFViUwklGDOZuvtg9isPXdBx5qUfRx73KmxGKLlwzHb+Wppb/j/avxF/CLYbBEgFefWtV60yVi06hW9Ob0xgjFW/F/IwZkvYzspo3OzZubaJ/HmSNzmn3saMSRDvcTiZWXxt7rrc6x5yhrdRNTBWVTJwzoS2dTqo6Zf8mJn5sp905mwkYbl9Yjwk1y541/JlUUrK6gfctAbOP218t4asz09Zq/Xm2Ayc0I6lwgXbjr7zBH4Sy5jNkOCdczGVT/tjRlV8D7qfNwR4evDK9Ow+Ev6a081SSe55MZ2Qiuq8qAUN4zOzdybyxF267SFBm7AsuCCc4zAnOvz6AM7Rh2bR1AaqobZyNHe6PHrvK2NTkPLpQ+vKM61b7q16nY0kaHJ0ABZwIZKS/kQQXi98/0aDRnIdM9RTcGPxubFJ4RHKEaxGkM86YjbK3/dlU1pCH8RmZK3hPiQIpWEXOZrfXPcFieZVTH1WMYyTqApzqcuWcNFBcUN6y+/5d72nhJkTBQ3r8LFLUeE2vPIIJOhq7EkTtR6T0oXbyiOKdhK+a+yJgwyVQW8UzB0j34="; 
        // System.out.println(encryptedPublic.length());
        //String b = "X9AsXcj3J9jrpasD4KKjZnZ8LpqpSB4qKoij/VzGRhLGTP3/oTDIIVe3kIQEBX2zNNvuIzV5hhXEs802ZSdfJ1ZaZZwi6NcULqNDJBDzlwAGWKcqc6T4mjOK2BNB//PsoVVKtAATVqsigYYcvGVShsA9sCHMWzsEW63ezq4Wmh145gDb/246PB4z7Ve8H48nzXfU8xjOUJylUk+rRdnc/BiyQLjM+hl20zlE/KYVKWIILDyRTLeU5/NKfLaA0q52XsE0qzGVCBzk7A1Zr2CAhI5cxU6qh3Rx48NWRh1a7z7PSfNXnTaLzbJpsHhn4tP6VKilij6q4X78YOFFwapmIgjpTgyQfz5LtsBCl6KKn69KAOD8Oj+RWhkzdbfUJzRhP29KNdrvRRf9i6vMEIHR/ucO1CL/hOpLRsPRZouIbu6ZKupE3XO8/BzxXxiHFzunWAfTql5OazQeVbboMLBJTQv1DSEVr0saCHKCIsQ/ModDX7o8S2skZIGMieHTVc2gqPfDKlTX0FU884nmZCLWBbCMA2Z+qXa1FeB4vQEmKf7pdpQjfIxhbjO5ZurQcFrATXeTc4why/dFWIaLtOkltIoOdCPMUVpplE+jscW74u5VlO91gjwlH533M5G4hDwiHjX6z1hMLfXI2GPxCVIHt9pB08xeBXHfpu18Ptl+dWA=";
        //System.out.println(encryptedPublic.equals(b));
        //PublicKey pk = KeyGenerator.loadAuthenticatorPublicKey();
        //System.out.println(Base64.getEncoder().encodeToString(pk.getEncoded()));
        //System.out.println("MODULUS PUBBLICO: " + ((java.security.interfaces.RSAPublicKey) pk).getModulus().toString(16).substring(0, 20));
        //PrivateKey prk = KeyGenerator.loadAuthenticatorPrivateKey();
        //System.out.println("MODULUS Privato: " + ((java.security.interfaces.RSAPrivateKey) prk).getModulus().toString(16).substring(0, 20));
        
        //String data = "{\"username\": \"pluto\", \"password\": \"1234\"}";
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
