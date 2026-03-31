package controller;

import enums.MenuOptions;
import enums.RoleOptions;
import exceptions.EmployeeDataAccessException;
import exceptions.EmployeeNotFoundException;
import exceptions.ValidationException;
import services.AuthService;
import services.EmployeeService;
import java.util.Scanner;
import dao.EmployeeDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuController {
	private static final Logger logger = LoggerFactory.getLogger(MenuController.class);
	public static boolean start(EmployeeDAO dao, Scanner sc) throws EmployeeDataAccessException {
		AuthService auth = new AuthService(dao, sc);
		EmployeeService employeeService = new EmployeeService(dao, sc, auth);
		try {
		    auth.login();
		} catch (ValidationException e) {
		    System.out.println(e.getMessage());
		    return false; 
		}
		// Force password change on first login
		if (auth.isFirstLogin()) {
			System.out.println("Change default password");
			auth.forceChangePassword();
		} 
		int attempts=0;
		while (true) {
			RoleOptions role = auth.getLoggedInRole();
			System.out.println();
			printMenu(role);
			System.out.print("Enter Choice: "); 
			String input = sc.nextLine().trim().toUpperCase(); 
		    if (input.isEmpty()) {
		        attempts++;
		        if (attempts >= 3) {
		            System.out.println("Too many invalid attempts.Logging out...");
		            auth.logout();
		            return true;
		        }
		        System.out.println("Input cannot be empty");
		        continue;
		    }
			MenuOptions choice;
			try {
				choice = MenuOptions.valueOf(input);
				attempts = 0;
			} catch (Exception e) {
				attempts++;
			    if (attempts >= 3) {
			        System.out.println("Too many invalid attempts.Logging out...");
			        auth.logout();
			        return true;
			    }
				System.out.println("Invalid choice. Try again");
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
				case LOGOUT: 
				    auth.logout();  
				    System.out.println("Logging out...");
					return true;
				default:
					break;
				}
			} catch (EmployeeNotFoundException | ValidationException e) {
				System.out.println(e.getMessage());
			}catch (Exception e) {
			    logger.error("Unexpected error", e);
			    System.out.println("Something went wrong");
			}
		}
	}
	// Validate user has required role
	private static void ensureRole(RoleOptions actual, RoleOptions required) {
	    if (actual == RoleOptions.ADMIN) {
	        return;
	    }
	    if (actual != required) {
	        throw new ValidationException("Access denied: " + required + " role required");
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
		System.out.println("LOGOUT");
	}
}