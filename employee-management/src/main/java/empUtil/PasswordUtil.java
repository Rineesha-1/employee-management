package empUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtil {
    private PasswordUtil() {}
    public static String sha1(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashedBytes=md.digest(password.getBytes());
            String hashedPassword = "";
            for(int i=0;i<hashedBytes.length;i++) {
            	hashedPassword+=String.format("%02x", hashedBytes[i]);
            }
            return hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing failed");
        }
    }
}