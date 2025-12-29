package methods;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//hashing
public class Password {
	public static String hash(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] messageDigest = md.digest(password.getBytes());
			BigInteger no = new BigInteger(1,messageDigest);
			String hashPassword = no.toString(16);
			return hashPassword;
		}
		catch(NoSuchAlgorithmException e) {
			System.out.println("Error in hashing password");
			return "";
		}
	}
}