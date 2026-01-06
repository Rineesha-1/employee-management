package services;

import controller.Input;
import enums.UpdateOptions;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidDataException;
import model.Employee;
import store.DataStore;
import empUtil.FileUtil;
import empUtil.PasswordUtil;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {
    private final DataStore store;
    public EmployeeService(DataStore store) {
        this.store = store;
    }
    public void addEmployee() throws InvalidDataException {
        Employee emp = new Employee();
        System.out.print("Enter emp name: ");
        emp.setName(Input.SC.nextLine());
        System.out.print("Enter emp dept: ");
        emp.setDepartment(Input.SC.nextLine());
        System.out.print("Enter emp address: ");
        emp.setAddress(Input.SC.nextLine());
        System.out.print("Enter emp email: ");
        emp.setEmail(Input.SC.nextLine());
        System.out.print("Enter emp role (ADMIN/MANAGER/USER): ");
        String role = normalizeRole(Input.SC.nextLine());
        emp.setRole(List.of(role));
        String id = nextId();
        emp.setId(id);
        emp.setPassword(PasswordUtil.sha1(AuthService.DEFAULT_PASS));
        store.getEmployees().put(emp.getId(), emp);
        FileUtil.saveStore(store);
        System.out.println("Employee inserted successfully");
        System.out.println("Login details: ID = " + emp.getId() + " , Default Password = " + AuthService.DEFAULT_PASS);
    }
    public void deleteEmployee(String id) throws EmployeeNotFoundException {
        Employee removed = store.getEmployees().remove(id.toLowerCase());
        if (removed == null) throw new EmployeeNotFoundException("Employee doesn't exist");
        FileUtil.saveStore(store);
        System.out.println("Employee deleted successfully");
    }
    public void resetPassword(String id) throws EmployeeNotFoundException {
        Employee emp = getEmployee(id);
        emp.setPassword(PasswordUtil.sha1(AuthService.DEFAULT_PASS));
        FileUtil.saveStore(store);
        System.out.println("Password reset to default");
    }
    public void grantRole(String id, String role) throws EmployeeNotFoundException, InvalidDataException {
        Employee emp = getEmployee(id);
        String r = normalizeRole(role);
        List<String> roles = new ArrayList<>(emp.getRole());
        for (String x : roles) {
            if (x.equalsIgnoreCase(r)) {
                System.out.println("Cannot assign same role again");
                return;
            }
        }
        roles.add(r);
        emp.setRole(roles);
        FileUtil.saveStore(store);
        System.out.println("Role granted");
    }
    public void revokeRole(String id, String role) throws EmployeeNotFoundException, InvalidDataException {
        Employee emp = getEmployee(id);
        String r = normalizeRole(role);
        List<String> roles = new ArrayList<>(emp.getRole());
        if (roles.size() <= 1) throw new InvalidDataException("At least one role must remain");

        boolean removed = roles.removeIf(x -> x != null && x.equalsIgnoreCase(r));
        if (!removed) {
            System.out.println("Role doesn't exist");
            return;
        }
        emp.setRole(roles);
        FileUtil.saveStore(store);
        System.out.println("Role revoked");
    }
    public void viewAll() throws InvalidDataException {
        if (store.getEmployees().isEmpty()) throw new InvalidDataException("No employees");
        System.out.println();
        System.out.println("Employee Details");
        System.out.println();
        for (Employee e : store.getEmployees().values()) {
            System.out.println(e);
        }
    }
    public void viewById(String id) throws EmployeeNotFoundException {
        Employee emp = getEmployee(id);
        System.out.println();
        System.out.println("Employee Detail");
        System.out.println();
        System.out.println(emp);
    }
    public void updateEmployee(String id, boolean selfUser) throws EmployeeNotFoundException, InvalidDataException {
        Employee emp = getEmployee(id);
        while (true) {
            System.out.println();
            System.out.println("Update Options:");
            System.out.println("ALL");
            if (!selfUser) {
                System.out.println("NAME");
                System.out.println("DEPARTMENT");
            }
            System.out.println("ADDRESS");
            System.out.println("EMAIL");
            System.out.println("BACK");
            System.out.print("Type your Choice: ");
            String c = Input.SC.nextLine().trim().toUpperCase();
            UpdateOptions choice;
            try {
                choice = UpdateOptions.valueOf(c);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid choice");
                continue;
            }
            if (choice == UpdateOptions.BACK) break;
            switch (choice) {
                case NAME: {
                    if (selfUser) throw new InvalidDataException("USER cannot update NAME");
                    System.out.print("Enter new name: ");
                    emp.setName(Input.SC.nextLine());
                }
                case DEPARTMENT: {
                    if (selfUser) throw new InvalidDataException("USER cannot update DEPARTMENT");
                    System.out.print("Enter new department: ");
                    emp.setDepartment(Input.SC.nextLine());
                }
                case ADDRESS: {
                    System.out.print("Enter new address: ");
                    emp.setAddress(Input.SC.nextLine());
                }
                case EMAIL: {
                    System.out.print("Enter new email: ");
                    emp.setEmail(Input.SC.nextLine());
                }
                case ALL: {
                    if (!selfUser) {
                        System.out.print("Enter new name: ");
                        emp.setName(Input.SC.nextLine());
                        System.out.print("Enter new department: ");
                        emp.setDepartment(Input.SC.nextLine());
                    }
                    System.out.print("Enter new address: ");
                    emp.setAddress(Input.SC.nextLine());
                    System.out.print("Enter new email: ");
                    emp.setEmail(Input.SC.nextLine());
                }
                default : {}
            }
            FileUtil.saveStore(store);
            System.out.println("Updated successfully");
        }
    }
    private Employee getEmployee(String id) throws EmployeeNotFoundException {
        if (id == null || id.trim().isEmpty()) throw new EmployeeNotFoundException("Employee doesn't exist");
        Employee emp = store.getEmployees().get(id.trim().toLowerCase());
        if (emp == null) throw new EmployeeNotFoundException("Employee doesn't exist");
        return emp;
    }
    private String nextId() {
        int next = store.getLastId() + 1;
        store.setLastId(next);
        return "tek" + next;
    }
    private String normalizeRole(String role) throws InvalidDataException {
        if (role == null) throw new InvalidDataException("Invalid role");
        String r = role.trim().toUpperCase();
        if (!(r.equals("ADMIN") || r.equals("MANAGER") || r.equals("USER"))) {
            throw new InvalidDataException("Invalid role");
        }
        return r;
    }
}