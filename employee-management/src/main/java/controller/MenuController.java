package controller;

import enums.MenuOptions;
import enums.RoleOptions;
import exceptions.EmployeeDataAccessException;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidDataException;
import services.AuthService;
import services.EmployeeService;
import java.util.Scanner;
import dao.EmployeeDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuController {
	private static final Logger logger = LoggerFactory.getLogger(MenuController.class);
	public static void start(EmployeeDAO dao, Scanner sc) throws EmployeeDataAccessException {
		AuthService auth = new AuthService(dao, sc);
		EmployeeService employeeService = new EmployeeService(dao, sc, auth);
		auth.login();
		// Force password change on first login
		if (auth.isFirstLogin()) {
			System.out.println("Change default password");
			auth.forceChangePassword();
		}
		boolean exit = false;
		int invalidMenuAttempts=0;
		while (!exit) {
			RoleOptions role = auth.getLoggedInRole();
			System.out.println();
			printMenu(role);
			System.out.print("Enter Choice: ");
			String input = null;
			int attempts = 0;
			while (attempts < 3) {
			    input = sc.nextLine().trim().toUpperCase();
			    if (!input.isEmpty()) break;
			    System.out.println("Input cannot be empty");
			    attempts++;
			}
			if (input == null || input.isEmpty()) {
			    System.out.println("Too many invalid attempts");
			    continue;
			}
			MenuOptions choice;
			try {
				choice = MenuOptions.valueOf(input);
				invalidMenuAttempts = 0;
			} catch (Exception e) {
				System.out.println("Invalid choice. Try again");
				invalidMenuAttempts++;
			    if (invalidMenuAttempts >= 3) {
			        System.out.println("Too many invalid attempts. Logging out...");
			        break;
			    }
				continue;
			}
			try {
				switch (choice) {
				case ADD:
					ensureRole(role, RoleOptions.ADMIN);
					employeeService.addEmployee();
					if (role != RoleOptions.USER) {
						employeeService.viewAll();
					}
					break;
				case VIEW:
					if (role == RoleOptions.USER) {
						employeeService.viewById(auth.getLoggedInId());
					} else {
						employeeService.viewEmployees();
					}
					break;
				case UPDATE:
					employeeService.updateEmployee(role, auth.getLoggedInId());
					if (role != RoleOptions.USER) {
						employeeService.viewAll();
					}
					break;
				case DELETE:
					ensureRole(role, RoleOptions.ADMIN);
					employeeService.deleteEmployee();
					if (role != RoleOptions.USER) {
						employeeService.viewAll();
					}
					break;
				case RESET_PASSWORD:
					ensureRole(role, RoleOptions.ADMIN);
					employeeService.resetPassword();
					break;
				case CHANGE_PASSWORD:
					auth.changePassword();
					break;
				case GRANT_ROLE:
					ensureRole(role, RoleOptions.ADMIN);
					employeeService.grantRole();
					if (role != RoleOptions.USER) {
						employeeService.viewAll();
					}
					break;
				case REVOKE_ROLE:
					ensureRole(role, RoleOptions.ADMIN);
					employeeService.revokeRole();
					if (role != RoleOptions.USER) {
						employeeService.viewAll();
					}
					break;
				case EXIT:
					exit = true;
					System.out.println("Logging out...");
					break;
				default:
					break;
				}
			} catch (EmployeeNotFoundException | InvalidDataException e) {
				System.out.println(e.getMessage());
			}catch (Exception e) {
			    logger.error("Unexpected error in MenuController", e);
			    System.out.println("Something went wrong");
			}
		}
	}
	// Validate user has required role
	private static void ensureRole(RoleOptions actual, RoleOptions required) throws InvalidDataException {
		if (actual != required) {
			throw new InvalidDataException("Access denied: " + required + " role required");
		}
	}
	// Displays menu options based on role
	private static void printMenu(RoleOptions role) {
		if (role == RoleOptions.ADMIN) {
			System.out.println("ADD");
			System.out.println("VIEW");
			System.out.println("UPDATE");
			System.out.println("DELETE");
			System.out.println("CHANGE_PASSWORD");
			System.out.println("RESET_PASSWORD");
			System.out.println("GRANT_ROLE");
			System.out.println("REVOKE_ROLE");
		} else if (role == RoleOptions.MANAGER) {
			System.out.println("VIEW");
			System.out.println("UPDATE");
			System.out.println("CHANGE_PASSWORD");
		} else {
			System.out.println("VIEW");
			System.out.println("UPDATE");
			System.out.println("CHANGE_PASSWORD");
		}
		System.out.println("EXIT");
	}
}