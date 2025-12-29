package exceptions;

public class EmployeeExistsException extends RuntimeException{
	public EmployeeExistsException(String s) {
		super(s);
	}
}
