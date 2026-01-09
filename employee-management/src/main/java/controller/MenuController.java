package controller;

import enums.MenuOptions;
import enums.RoleOptions;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidDataException;
import services.AuthService;
import services.EmployeeService;
import store.DataStore;
import java.util.Scanner;
import dao.EmployeeDAO;

public class MenuController {
    private MenuController() {
    }
    public static void start(DataStore store,EmployeeDAO dao) {
    	Scanner sc = new Scanner(System.in);
        AuthService auth = new AuthService(store,dao,sc);
        EmployeeService employeeService = new EmployeeService(store,dao,sc);
        auth.login();
        if (auth.isFirstLogin()) {
            System.out.println("Change default password.");
            auth.forceChangePassword();
        }
        boolean exit = false;
        while (!exit) {
            RoleOptions role = auth.getLoggedInRole();
            System.out.println();
            System.out.println("EMPLOYEE MANAGEMENT SYSTEM");
            System.out.println("Logged in as: " + role);
            printMenu(role);
            System.out.print("Enter Choice: ");
            String input = sc.nextLine().trim().toUpperCase();
            MenuOptions choice;
            try {
                choice = MenuOptions.valueOf(input);
            } catch (Exception e) {
                System.out.println("Invalid choice");
                continue;
            }
            try {
                switch (choice) {
                    case ADD:
                        ensureRole(role, RoleOptions.ADMIN);
                        employeeService.addEmployee();
                        break;
                    case VIEW:
                        if (role == RoleOptions.USER) {
                            employeeService.viewById(auth.getLoggedInId());
                        } else {
                            System.out.print("Enter ID or ALL: ");
                            String v = sc.nextLine().trim();
                            if (v.equalsIgnoreCase("ALL")) {
                                employeeService.viewAll();
                            } else {
                                employeeService.viewById(v);
                            }
                        }
                        break;
                    case UPDATE:
                        if (role == RoleOptions.USER) {
                            employeeService.updateEmployee(auth.getLoggedInId(), true);
                        } else {
                            System.out.print("Enter employee id: ");
                            String id = sc.nextLine().trim();
                            employeeService.updateEmployee(id, false);
                        }
                        break;
                    case DELETE:
                        ensureRole(role, RoleOptions.ADMIN);
                        System.out.print("Enter empId to delete: ");
                        employeeService.deleteEmployee(sc.nextLine());
                        break;
                    case RESET_PASSWORD:
                        ensureRole(role, RoleOptions.ADMIN);
                        System.out.print("Enter employee id: ");
                        employeeService.resetPassword(sc.nextLine());
                        break;
                    case CHANGE_PASSWORD:
                        auth.changePassword();
                        break;
                    case GRANT_ROLE:
                        ensureRole(role, RoleOptions.ADMIN);
                        System.out.print("Enter employee id: ");
                        String gid = sc.nextLine();
                        System.out.print("Enter role to grant (ADMIN/MANAGER/USER): ");
                        String gRole = sc.nextLine();
                        employeeService.grantRole(gid, gRole);
                        break;

                    case REVOKE_ROLE:
                        ensureRole(role, RoleOptions.ADMIN);
                        System.out.print("Enter employee id: ");
                        String rid = sc.nextLine();
                        System.out.print("Enter role to revoke (ADMIN/MANAGER/USER): ");
                        String rRole = sc.nextLine();
                        employeeService.revokeRole(rid, rRole);
                        break;
                    case EXIT:
                        exit = true;
                        break;
				default:
					break;
                }
            } catch (EmployeeNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (InvalidDataException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Something went wrong");
            }
        }
    }
    private static void ensureRole(RoleOptions actual, RoleOptions required)
            throws InvalidDataException {
        if (actual != required) {
            throw new InvalidDataException("Access denied");
        }
    }
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
            System.out.println("EXIT");
        } else if (role == RoleOptions.MANAGER) {
            System.out.println("VIEW");
            System.out.println("UPDATE");
            System.out.println("CHANGE_PASSWORD");
            System.out.println("EXIT");
        } else {
            System.out.println("VIEW");
            System.out.println("UPDATE");
            System.out.println("CHANGE_PASSWORD");
            System.out.println("EXIT");
        }
    }
}