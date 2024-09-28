package de.vd40xu.smilebase.service.utility;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class PSUtility {

    @Value("${smilebase.PS_KEY}") String keyHolder;
    @Value("${smilebase.PS_TOKEN}") String tokenHolder;

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    private static String key;
    private static String token;

    @PostConstruct
    public void init() {
        key = keyHolder;
        token = tokenHolder;
    }

    public static boolean validateToken(String receivedHash) {
        try {
            String generatedHash = generateHmacSha256(token, key);
            return generatedHash.equals(receivedHash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return false;
        }
    }

    public static String generateHmacSha256(String message, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256_ALGORITHM);
        mac.init(secretKeySpec);
        byte[] hmacSha256 = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacSha256);
    }
}
