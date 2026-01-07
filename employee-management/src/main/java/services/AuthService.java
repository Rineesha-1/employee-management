package services;

import controller.Input;
import enums.RoleOptions;
import exceptions.InvalidDataException;
import model.Employee;
import store.DataStore;
import empUtil.FileUtil;
import empUtil.PasswordUtil;

public class AuthService {
    public static final String DEFAULT_PASS = "pass123";
    private DataStore store;
    private String loggedInId;
    private RoleOptions loggedInRole;
    private boolean firstLogin;
    public AuthService(DataStore store) {
        this.store = store;
    }
    public void login() {
        while (true) {
            System.out.println();
            System.out.println("LOGIN");
            System.out.print("Enter Employee ID: ");
            String id = Input.SC.nextLine().trim().toLowerCase();
            System.out.print("Enter Password: ");
            String password = Input.SC.nextLine().trim();
            Employee emp = store.getEmployees().get(id);
            if (emp == null) {
                System.out.println("Invalid login, try again.");
                continue;
            }
            String hashedPassword = PasswordUtil.sha1(password);
            if (!hashedPassword.equals(emp.getPassword())) {
                System.out.println("Invalid login, try again.");
                continue;
            }
            loggedInId = emp.getId();
            loggedInRole = getRole(emp);
            firstLogin = emp.getPassword().equals(PasswordUtil.sha1(DEFAULT_PASS));
            System.out.println("Login Successful");
            return;
        }
    }
    public void changePassword() throws InvalidDataException {
        Employee emp = store.getEmployees().get(loggedInId);
        if (emp == null) {
            throw new InvalidDataException("Login expired");
        }
        System.out.print("Enter new password: ");
        String pass1 = Input.SC.nextLine().trim();
        System.out.print("Re-enter new password: ");
        String pass2 = Input.SC.nextLine().trim();
        if (!pass1.equals(pass2)) {
            throw new InvalidDataException("Passwords do not match");
        }
        if (pass1.equals(DEFAULT_PASS)) {
            throw new InvalidDataException(
                    "New password cannot be default password");
        }
        emp.setPassword(PasswordUtil.sha1(pass1));
        FileUtil.saveStore(store);
        firstLogin = false;
        System.out.println("Password changed successfully");
    }
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
    private RoleOptions getRole(Employee emp) {
        if (emp.hasRole("ADMIN")) {
            return RoleOptions.ADMIN;
        }
        if (emp.hasRole("MANAGER")) {
            return RoleOptions.MANAGER;
        }
        return RoleOptions.USER;
    }
}
