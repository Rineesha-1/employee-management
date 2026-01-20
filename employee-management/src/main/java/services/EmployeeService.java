package services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import dao.EmployeeDAO;
import empUtil.ConfigUtil;
import empUtil.PasswordUtil;
import enums.RoleOptions;
import enums.UpdateOptions;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidDataException;
import model.Employee;

public class EmployeeService {
	private final EmployeeDAO dao;
	private final Scanner sc;
	private final AuthService auth;

	public EmployeeService(EmployeeDAO dao, Scanner sc, AuthService auth) {
		this.dao = dao;
		this.sc = sc;
		this.auth = auth;
	}

	// validates id format tek<number>
	private String normalizeAndValidateId(String id) throws InvalidDataException {
		if (id == null || id.trim().isEmpty())
			throw new InvalidDataException("Employee id cannot be null");
		if (!id.matches("(?i)tek\\d+"))
			throw new InvalidDataException("Invalid id format");
		return id.trim().toLowerCase();
	}

	// validates role input
	private String normalizeRole(String role) throws InvalidDataException {
		if (role == null || role.trim().isEmpty())
			throw new InvalidDataException("Role cannot be empty");
		try {
			return RoleOptions.valueOf(role.trim().toUpperCase()).name();
		} catch (IllegalArgumentException e) {
			throw new InvalidDataException("Invalid role");
		}
	}

	// validates email format
	private String normalizeEmail(String email) throws InvalidDataException {
		if (email == null || email.trim().isEmpty())
			throw new InvalidDataException("Email cannot be empty");
		String e = email.trim().toLowerCase();
		if (!e.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
			throw new InvalidDataException("Invalid email format");
		return e;
	}

	// validates other fields like name,address,dept
	private String validateNotBlank(String value, String field) throws InvalidDataException {
		if (value == null || value.trim().isEmpty()) {
			throw new InvalidDataException(field + " cannot be empty");
		}
		return value.trim();
	}

	// creates random temporary password
	private String generateTempPassword() {
		return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
	}

	// adds new employee with a random password
	public void addEmployee() {
	    String name;
	    while (true) {
	        try {
	            System.out.print("Enter emp name: ");
	            name = validateNotBlank(sc.nextLine(), "Name");
	            break;
	        } catch (InvalidDataException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	    String dept;
	    while (true) {
	        try {
	            System.out.print("Enter emp dept: ");
	            dept = validateNotBlank(sc.nextLine(), "Department");
	            break;
	        } catch (InvalidDataException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	    String address;
	    while (true) {
	        try {
	            System.out.print("Enter emp address: ");
	            address = validateNotBlank(sc.nextLine(), "Address");
	            break;
	        } catch (InvalidDataException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	    String email;
	    while (true) {
	        try {
	            System.out.print("Enter emp email: ");
	            email = normalizeEmail(sc.nextLine());
	            break;
	        } catch (InvalidDataException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	    String role;
	    while (true) {
	        try {
	            System.out.print("Enter emp role (ADMIN/MANAGER/USER): ");
	            role = normalizeRole(sc.nextLine());
	            break;
	        } catch (InvalidDataException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	    List<String> roles = new ArrayList<>();
	    roles.add(role);
	    String tempPassword = generateTempPassword();
	    String empId = dao.addEmployee(name, dept, address, email,roles, PasswordUtil.sha256(tempPassword));
	    System.out.println("Employee added successfully");
	    System.out.println("Employee ID   : " + empId);
	    System.out.println("Temp Password : " + tempPassword);
	}

	// deletes employee based on id
	public void deleteEmployee() throws InvalidDataException, EmployeeNotFoundException {
		System.out.print("Enter empId to delete: ");
		String empId = normalizeAndValidateId(sc.nextLine());
		// prevents deleting own account
		if (empId.equals(auth.getLoggedInId())) {
			throw new InvalidDataException("Cannot delete your own account");
		}
		if (empId.equals(ConfigUtil.getDefaultAdminId())) {
			throw new InvalidDataException("Cannot delete default admin account");
		}
		dao.getEmployeeById(empId);
		dao.deleteEmployee(empId);
		System.out.println("Employee deleted successfully");
	}

	// resets individual password and generates temporary password
	public void resetPassword() throws InvalidDataException, EmployeeNotFoundException {
		System.out.print("Enter employee id: ");
		String empId = normalizeAndValidateId(sc.nextLine());
		dao.getEmployeeById(empId);
		String tempPassword = generateTempPassword();
		dao.resetPassword(empId, PasswordUtil.sha256(tempPassword));
		System.out.println("Password reset successfully");
		System.out.println("Temporary password: " + tempPassword);
	}

	// grants role for existing employee
	public void grantRole() throws InvalidDataException, EmployeeNotFoundException {
		System.out.print("Enter employee id: ");
		String empId = normalizeAndValidateId(sc.nextLine());
		Employee emp = dao.getEmployeeById(empId);
		System.out.println("Current roles: " + emp.getRole());
		System.out.print("Enter role to grant (ADMIN/MANAGER/USER): ");
		String role = normalizeRole(sc.nextLine());
		if (empId.equals(ConfigUtil.getDefaultAdminId()) && role.equals("ADMIN")) {
			throw new InvalidDataException("Default admin already has this role");
		}
		dao.grantRole(empId, role);
		System.out.println("Role granted successfully");
	}

	// revokes role from existing employee
	public void revokeRole() throws InvalidDataException, EmployeeNotFoundException {
		System.out.print("Enter employee id: ");
		String empId = normalizeAndValidateId(sc.nextLine());
		if (empId.equals(ConfigUtil.getDefaultAdminId())) {
			throw new InvalidDataException("Cannot modify default admin's roles");
		}
		Employee emp = dao.getEmployeeById(empId);
		System.out.println("Current roles: " + emp.getRole());
		System.out.print("Enter role to revoke (ADMIN/MANAGER/USER): ");
		String role = normalizeRole(sc.nextLine());
		if (empId.equals(auth.getLoggedInId()) && role.equals("ADMIN")) {
			throw new InvalidDataException("Cannot revoke your own ADMIN role");
		}
		dao.revokeRole(empId, role);
		System.out.println("Role revoked successfully");
	}

	// displays all employees
	public void viewAll() {
		List<Employee> employees = dao.getAllEmployees();
		if (employees.isEmpty()) {
			System.out.println("No employees found");
			return;
		}
		for (Employee emp : employees) {
			System.out.println(emp);
		}
	}

	// displays individual record of an employee based on id
	public void viewById(String id) throws InvalidDataException, EmployeeNotFoundException {
		String empId = normalizeAndValidateId(id);
		Employee emp = dao.getEmployeeById(empId);
		System.out.println(emp);
	}

	// displays all employees or specific id details
	public void viewEmployees() throws InvalidDataException, EmployeeNotFoundException {
		System.out.print("Enter ID or ALL: ");
		String input = sc.nextLine().trim();
		if (input.equalsIgnoreCase("ALL")) {
			viewAll();
		} else {
			viewById(input);
		}
	}

	// updates existing employees based on the fields selected
	public void updateEmployee(RoleOptions role, String loggedInId)
			throws InvalidDataException, EmployeeNotFoundException {
		String empId;
		if (role == RoleOptions.USER) {
			empId = normalizeAndValidateId(loggedInId);
		} else {
			System.out.print("Enter employee id: ");
			empId = normalizeAndValidateId(sc.nextLine());
		}
		Employee emp = dao.getEmployeeById(empId);
		boolean isPrivileged = role == RoleOptions.ADMIN || role == RoleOptions.MANAGER;

		while (true) {
			System.out.println("Update Options");
			if (isPrivileged) {
				System.out.println("NAME");
				System.out.println("DEPARTMENT");
				System.out.println("ALL");
			}
			System.out.println("ADDRESS");
			System.out.println("EMAIL");
			System.out.println("BACK");
			System.out.print("Choice: ");
			String input = sc.nextLine().trim().toUpperCase();
			UpdateOptions option;
			try {
				option = UpdateOptions.valueOf(input);
			} catch (IllegalArgumentException e) {
				System.out.println("Invalid choice");
				continue;
			}
			if (option == UpdateOptions.BACK)
				break;
			try {
				switch (option) {
				case NAME:
					if (!isPrivileged)
						throw new InvalidDataException("Only ADMIN or MANAGER can update name");
					System.out.print("Enter new name: ");
					emp.setName(validateNotBlank(sc.nextLine(), "Name"));
					break;
				case DEPARTMENT:
					if (!isPrivileged)
						throw new InvalidDataException("Only ADMIN or MANAGER can update department");
					System.out.print("Enter new department: ");
					emp.setDepartment(validateNotBlank(sc.nextLine(), "Department"));
					break;
				case ADDRESS:
					System.out.print("Enter new address: ");
					emp.setAddress(validateNotBlank(sc.nextLine(), "Address"));
					break;
				case EMAIL:
					System.out.print("Enter new email: ");
					String email = normalizeEmail(sc.nextLine());
					emp.setEmail(email);
					break;
				case ALL:
					if (!isPrivileged)
						throw new InvalidDataException("Only ADMIN or MANAGER can update all fields");
					System.out.print("Enter new name: ");
					emp.setName(validateNotBlank(sc.nextLine(), "Name"));
					System.out.print("Enter new department: ");
					emp.setDepartment(validateNotBlank(sc.nextLine(), "Department"));
					System.out.print("Enter new address: ");
					emp.setAddress(validateNotBlank(sc.nextLine(), "Address"));
					System.out.print("Enter new email: ");
					String allEmail = normalizeEmail(sc.nextLine());
					emp.setEmail(allEmail);
					break;
				case BACK:
					return;
				}
				dao.updateEmployee(emp.getId(), emp.getName(), emp.getDepartment(), emp.getAddress(), emp.getEmail());
				System.out.println("Updated successfully");
			} catch (InvalidDataException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}