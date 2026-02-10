package services;

import enums.RoleOptions;
import dao.EmployeeDAO;
import exceptions.EmployeeDataAccessException;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidDataException;
import model.Employee;
import empUtil.PasswordUtil;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AuthService {
	private final EmployeeDAO dao;
	private final Scanner sc;
	private String loggedInId;
	private RoleOptions loggedInRole;
	private boolean firstLogin;
	private static final Logger logger =LoggerFactory.getLogger(AuthService.class);
	private static final int MAX_LOGIN_ATTEMPTS = 3;
	private static final int MAX_PASSWORD_CHANGE_ATTEMPTS=3;
	public AuthService(EmployeeDAO dao, Scanner sc) {
		this.dao = dao;
		this.sc = sc;
	}

	// basic rule for password
	private void validatePassword(String password) throws InvalidDataException {
		if (password == null) {
			throw new InvalidDataException("Password can't be null");
		}
		String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$";
		if (!password.matches(passwordRegex)) {
	        throw new InvalidDataException(
	            "Password must be at least 8 characters" +
	            "(1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character)"
	        );
	    }
	}

	// login verification
	public void login() throws EmployeeDataAccessException {
		int attempts=0;
		while (attempts<MAX_LOGIN_ATTEMPTS) {
			logger.info("LOGIN CREDENTIALS");
			System.out.print("Employee ID: ");
			String id = sc.nextLine().trim().toLowerCase();
			System.out.print("Password: ");
			String password = sc.nextLine().trim();
			if (id.isEmpty() || password.isEmpty()) {
	            logger.warn("Employee ID or password is empty");
	            attempts++;
	            continue;
	        }
			try {
				Employee emp = dao.getEmployeeById(id); 
				if (emp.isDeleted() || !PasswordUtil.sha256(password).equals(emp.getPassword())) {
					logger.warn("Invalid login credentials for employeeId={}", id);
					attempts++;
					continue;
				}
				loggedInId = emp.getId();
				loggedInRole = resolveRole(emp);
				firstLogin = emp.isFirstLogin();
				logger.info("Login successful for employeeId={}", loggedInId);
				return;
			} catch (EmployeeNotFoundException e) {
				logger.warn("Invalid login attempt for employeeId={}", id);
				attempts++;
			}
		}
		logger.error("Maximum login attempts reached.Exiting...");
		throw new RuntimeException("Login failed");
	}

	// enables employee to change password
	public void changePassword() throws InvalidDataException, EmployeeNotFoundException, EmployeeDataAccessException {
		System.out.print("Enter new password: ");
		String newPassword = sc.nextLine().trim();
		validatePassword(newPassword);
		System.out.print("Re-enter new password: ");
		String confirmPassword = sc.nextLine().trim();
		if (!newPassword.equals(confirmPassword))
			throw new InvalidDataException("Passwords do not match");
		Employee emp = dao.getEmployeeById(loggedInId);
		String newHash = PasswordUtil.sha256(newPassword);
		if (newHash.equals(emp.getPassword()))
			throw new InvalidDataException("New password cannot be same as old password");
		dao.changePassword(loggedInId, newHash);
		firstLogin = false;
		logger.info("Password changed successfully for employeeId={}", loggedInId);
	}

	// to change default password on first login
	public void forceChangePassword() {
		int attempts=0;
		while (attempts<MAX_PASSWORD_CHANGE_ATTEMPTS) {
			try {
				changePassword();
				return;
			} catch (Exception e) {
				logger.warn("Password change failed: {}", e.getMessage());
				attempts++;
			}
		}
		logger.error("Maximum password change attempts reached for employeeId={}", loggedInId);
		throw new RuntimeException("Password change failed");
	}
	
//	public void logout() {
//	    loggedInId = null;
//	    loggedInRole = null;
//	    firstLogin = false;
//	    System.out.println("Logged out successfully");
//	}
	
	public boolean isFirstLogin() {
		return firstLogin;
	}

	public String getLoggedInId() {
		return loggedInId;
	}

	public RoleOptions getLoggedInRole() {
		return loggedInRole;
	}

	// highest role for employee
	private RoleOptions resolveRole(Employee emp) {
		if (emp.hasRole(RoleOptions.ADMIN.name()))
			return RoleOptions.ADMIN;
		if (emp.hasRole(RoleOptions.MANAGER.name()))
			return RoleOptions.MANAGER;
		return RoleOptions.USER;
	}
}