package de.vd40xu.smilebase.service.utility;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PSUtility {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    @Value("${smilebase.PS_KEY}")
    private static String key;
    @Value("${smilebase.PS_TOKEN}")
    private static String token;

    public PSUtility() {
        throw new IllegalStateException("Utility class");
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
