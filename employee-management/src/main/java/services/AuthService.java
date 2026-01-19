package services;

import enums.RoleOptions;
import dao.EmployeeDAO;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidDataException;
import model.Employee;
import empUtil.PasswordUtil;
import java.util.Scanner;

public class AuthService {
	private final EmployeeDAO dao;
	private final Scanner sc;
	private String loggedInId;
	private RoleOptions loggedInRole;
	private boolean firstLogin;

	public AuthService(EmployeeDAO dao, Scanner sc) {
		this.dao = dao;
		this.sc = sc;
	}

	// basic rule for password
	private void validatePassword(String password) throws InvalidDataException {
		if (password == null || password.length() < 6) {
			throw new InvalidDataException("Password must be atleast 6 characters");
		}
	}

	// login verification
	public void login() {
		while (true) {
			System.out.println("LOGIN CREDENTIALS");
			System.out.print("Employee ID: ");
			String id = sc.nextLine().trim().toLowerCase();
			System.out.print("Password: ");
			String password = sc.nextLine().trim();
			try {
				Employee emp = dao.getEmployeeById(id);
				if (!PasswordUtil.sha256(password).equals(emp.getPassword())) {
					System.out.println("Invalid login credentials");
					continue;
				}
				loggedInId = emp.getId();
				loggedInRole = resolveRole(emp);
				firstLogin = emp.isFirstLogin();
				System.out.println("Login successful");
				return;
			} catch (EmployeeNotFoundException e) {
				System.out.println("Invalid login credentials");
			}
		}
	}

	// enables employee to change password
	public void changePassword() throws InvalidDataException, EmployeeNotFoundException {
		System.out.print("Enter new password: ");
		String p1 = sc.nextLine().trim();
		validatePassword(p1);
		System.out.print("Re-enter new password: ");
		String p2 = sc.nextLine().trim();
		if (!p1.equals(p2))
			throw new InvalidDataException("Passwords do not match");
		Employee emp = dao.getEmployeeById(loggedInId);
		String newHash = PasswordUtil.sha256(p1);
		if (newHash.equals(emp.getPassword()))
			throw new InvalidDataException("New password cannot be same as old password");
		dao.changePassword(loggedInId, newHash);
		firstLogin = false;
		System.out.println("Password changed successfully");
	}

	// to change default password on first login
	public void forceChangePassword() {
		while (true) {
			try {
				changePassword();
				return;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

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