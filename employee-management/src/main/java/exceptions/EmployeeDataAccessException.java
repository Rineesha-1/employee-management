package exceptions;

public class EmployeeDataAccessException extends Exception {
	public EmployeeDataAccessException(String message,Throwable cause) {
		super(message,cause);
	}
}
