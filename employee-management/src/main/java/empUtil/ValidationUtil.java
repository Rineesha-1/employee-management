package empUtil;

import enums.RoleOptions;
import exceptions.ValidationException;

public final class ValidationUtil {
	private ValidationUtil() {}
	public static String validateId(String id) {
        if (id == null || id.trim().isEmpty())
            throw new ValidationException("Invalid employee ID");
        if (!id.matches("(?i)tek\\d+"))
            throw new ValidationException("Invalid employee ID");
        return id.trim().toLowerCase();
    }

    public static String validateRole(String role) {
        if (role == null || role.trim().isEmpty())
            throw new ValidationException("Invalid role");
        try {
            return RoleOptions.valueOf(role.trim().toUpperCase()).name();
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid role");
        }
    }

    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty())
            throw new ValidationException("Invalid email");
        String Email = email.trim().toLowerCase();
        if (!Email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            throw new ValidationException("Invalid email");
        return Email;
    }
    
	public static void validatePassword(String password){
		if (password == null) {
			throw new ValidationException("Password can't be null");
		}
		String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$";
		if (!password.matches(passwordRegex)) {
			throw new ValidationException(
	            "Password must be at least 8 characters" +
	            "(1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character)"
	        );
	    }
	}
    
    public static String validateNotBlank(String value, String field) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(field + " cannot be empty");
        }
        return value.trim();
    }
}
