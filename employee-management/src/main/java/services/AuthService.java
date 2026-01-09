package services;

import enums.RoleOptions;
import dao.EmployeeDAO;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidDataException;
import model.Employee;
import store.DataStore;
import empUtil.PasswordUtil;

import java.util.Scanner;

public class AuthService { 
    private final DataStore store;
    private final EmployeeDAO dao;
    private final Scanner sc;
    private String loggedInId;
    private RoleOptions loggedInRole;
    private boolean firstLogin;
    
    public AuthService(DataStore store, EmployeeDAO dao, Scanner sc) {
        this.store = store;
        this.dao = dao;
        this.sc = sc;
    }
    
    public static String hash(String rawPassword) {
        return PasswordUtil.sha256(rawPassword);
    }

    public void login() { 
    	while (true) {
        	System.out.println("LOGIN CREDENTIALS");
            System.out.print("Employee ID: ");
            String id = sc.nextLine().trim().toLowerCase();
            System.out.print("Password: ");
            String password = sc.nextLine().trim();
            Employee emp = store.getEmployees().get(id);
            if (emp == null || !hash(password).equals(emp.getPassword())) {
                System.out.println("Invalid login");
                continue;
            }
            loggedInId = emp.getId();
            loggedInRole = resolveRole(emp);
            firstLogin = emp.isFirstLogin(); 
            System.out.println("Login successful");
            return;
        }
    }

    public void changePassword() throws InvalidDataException, EmployeeNotFoundException {
        Employee emp = store.getEmployees().get(loggedInId);
        if (emp == null) throw new InvalidDataException("Login expired");
        System.out.print("Enter new password: ");
        String p1 = sc.nextLine().trim();
        System.out.print("Re-enter new password: ");
        String p2 = sc.nextLine().trim();
        if (!p1.equals(p2)) throw new InvalidDataException("Passwords do not match");
        String newHash = hash(p1); 
        if (newHash.equals(emp.getPassword()))
            throw new InvalidDataException("New password cannot be same as old password");
        dao.changePassword(emp.getId(), newHash);
        emp.setPassword(newHash);
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

    private RoleOptions resolveRole(Employee emp) {
        if (emp.hasRole(RoleOptions.ADMIN.name())) return RoleOptions.ADMIN;
        if (emp.hasRole(RoleOptions.MANAGER.name())) return RoleOptions.MANAGER;
        return RoleOptions.USER;
    }
}
