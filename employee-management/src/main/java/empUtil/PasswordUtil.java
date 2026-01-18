package empUtil;

import java.security.MessageDigest;

public final class PasswordUtil {
	private PasswordUtil() {
	}

	public static String sha256(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : hashBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException("Password hashing failed");
		}
	}
}
