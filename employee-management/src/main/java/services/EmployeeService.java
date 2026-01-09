package services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import dao.EmployeeDAO;
import enums.RoleOptions;
import enums.UpdateOptions;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidDataException;
import model.Employee;
import store.DataStore;

public class EmployeeService {
    private final DataStore store;
    private final EmployeeDAO dao;
    private final Scanner sc;
    public EmployeeService(DataStore store, EmployeeDAO dao, Scanner sc) {
        this.store = store;
        this.dao = dao;
        this.sc = sc;
    } 
    private String normalizeAndValidateId(String id) throws InvalidDataException {
        if (id == null)
            throw new InvalidDataException("Employee id cannot be null");
        if (!id.matches("(?i)tek\\d+"))
            throw new InvalidDataException("Invalid employee id format");
        return id.trim().toLowerCase();
    }
    
    private String normalizeRole(String role) throws InvalidDataException {
        if (role == null) throw new InvalidDataException("Role cannot be null");
        try {
            return RoleOptions.valueOf(role.trim().toUpperCase()).name();
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Invalid role");
        }
    }

    private String normalizeEmail(String email) throws InvalidDataException {
        if (email == null)
            throw new InvalidDataException("Email cannot be null");
        String e = email.trim().toLowerCase();
        if (!e.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            throw new InvalidDataException("Invalid email format");
        return e;
    }
    private String validateNotBlank(String value, String field) throws InvalidDataException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDataException(field + " cannot be empty");
        }
        return value.trim();
    }

    private String generateTempPassword() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    } 
    public void addEmployee() throws InvalidDataException {
        Employee emp = new Employee();
        System.out.print("Enter emp name: ");
        emp.setName(validateNotBlank(sc.nextLine(), "Name"));
        System.out.print("Enter emp dept: ");
        emp.setDepartment(validateNotBlank(sc.nextLine(), "Dept"));
        System.out.print("Enter emp address: ");
        emp.setAddress(validateNotBlank(sc.nextLine(), "Address"));
        System.out.print("Enter emp email: ");
        emp.setEmail(normalizeEmail(sc.nextLine()));
        System.out.print("Enter emp role (ADMIN/MANAGER/USER): ");
        String role = normalizeRole(sc.nextLine());
        List<String> roles = new ArrayList<>();
        roles.add(role);
        String tempPassword = generateTempPassword();
        String hash = AuthService.hash(tempPassword);
        emp.setFirstLogin(true);
        String empId = dao.addEmployee(emp.getName(),emp.getDepartment(),emp.getAddress(),emp.getEmail(),roles,hash);
        System.out.println("Employee inserted successfully");
        System.out.println("Employee ID   : " + empId);
        System.out.println("Temp Password : " + tempPassword);
    }
    
    public void deleteEmployee(String id) throws InvalidDataException, EmployeeNotFoundException {
        String empId = normalizeAndValidateId(id);
        dao.deleteEmployee(empId);
        System.out.println("Employee deleted successfully");
    }

    public void resetPassword(String id) throws InvalidDataException {
        String empId = normalizeAndValidateId(id);
        String tempPassword = generateTempPassword();
        dao.resetPassword(empId, AuthService.hash(tempPassword));
        System.out.println("Temporary password: " + tempPassword);
    } 
    public void grantRole(String id, String role) throws InvalidDataException {
        String empId = normalizeAndValidateId(id);
        String normalizedRole = normalizeRole(role);
        dao.grantRole(empId, normalizedRole);
    }

    public void revokeRole(String id, String role) throws InvalidDataException {
        String empId = normalizeAndValidateId(id);
        String normalizedRole = normalizeRole(role);
        dao.revokeRole(empId, normalizedRole);
    }

    public void viewAll() {
        dao.viewEmployee();
    }

    public void viewById(String id) throws InvalidDataException {
        String empId = normalizeAndValidateId(id);
        dao.viewEmployeeById(empId);
    } 
    
    public void updateEmployee(String id, boolean selfUser) throws InvalidDataException, EmployeeNotFoundException {
        String empId = normalizeAndValidateId(id);
        Employee emp = store.getEmployees().get(empId);
        if (emp == null)
            throw new EmployeeNotFoundException("Employee doesn't exist");
        while (true) {
            System.out.println("\nUpdate Options:");
            System.out.println("ALL");
            if (!selfUser) {
                System.out.println("NAME");
                System.out.println("DEPARTMENT");
            }
            System.out.println("ADDRESS");
            System.out.println("EMAIL");
            System.out.println("BACK");
            System.out.print("Choice: ");
            UpdateOptions option;
            try {
                option = UpdateOptions.valueOf(sc.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid choice");
                continue;
            }
            if (option == UpdateOptions.BACK) break;
            switch (option) {
            case NAME:
                if (selfUser)
                    throw new InvalidDataException("USER cannot update NAME");
                System.out.print("Enter new name: ");
                emp.setName(validateNotBlank(sc.nextLine(), "Name"));
                break;
            case DEPARTMENT:
                if (selfUser)
                    throw new InvalidDataException("USER cannot update DEPARTMENT");
                System.out.print("Enter new department: ");
                emp.setDepartment(validateNotBlank(sc.nextLine(), "Department"));
                break;
            case ADDRESS:
                System.out.print("Enter new address: ");
                emp.setAddress(validateNotBlank(sc.nextLine(), "Address"));
                break;
            case EMAIL:
                System.out.print("Enter new email: ");
                emp.setEmail(normalizeEmail(sc.nextLine()));
                break;
            case ALL:
                if (!selfUser) {
                    System.out.print("Enter new name: ");
                    emp.setName(validateNotBlank(sc.nextLine(), "Name"));

                    System.out.print("Enter new department: ");
                    emp.setDepartment(validateNotBlank(sc.nextLine(), "Department"));
                }
                System.out.print("Enter new address: ");
                emp.setAddress(validateNotBlank(sc.nextLine(), "Address"));
                System.out.print("Enter new email: ");
                emp.setEmail(normalizeEmail(sc.nextLine()));
                break;
            default:
                System.out.println("Invalid option");
            }
            dao.updateEmployee(emp.getId(),emp.getName(),emp.getDepartment(),emp.getAddress(),emp.getEmail()
            );
            System.out.println("Updated successfully");
        }
    }
}
