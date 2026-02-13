package services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import dao.EmployeeDAO;
import empUtil.ConfigUtil;
import empUtil.PasswordUtil;
import enums.RoleOptions;
import enums.UpdateOptions;
import exceptions.EmployeeDataAccessException;
import exceptions.EmployeeNotFoundException;
import exceptions.ValidationException;
import model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeService {
    private final EmployeeDAO dao;
    private final Scanner sc;
    private final AuthService auth;
    private static final int MAX_INPUT_ATTEMPTS = 3;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    public EmployeeService(EmployeeDAO dao, Scanner sc, AuthService auth) {
        this.dao = dao;
        this.sc = sc;
        this.auth = auth;
    }

    private String normalizeAndValidateId(String id) {
        if (id == null || id.trim().isEmpty())
            throw new ValidationException("Invalid employee ID");
        if (!id.matches("(?i)tek\\d+"))
            throw new ValidationException("Invalid employee ID");
        return id.trim().toLowerCase();
    }

    private String normalizeRole(String role) {
        if (role == null || role.trim().isEmpty())
            throw new ValidationException("Invalid roles");
        try {
            return RoleOptions.valueOf(role.trim().toUpperCase()).name();
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid roles");
        }
    }

    private String normalizeEmail(String email) {
        if (email == null || email.trim().isEmpty())
            throw new ValidationException("Invalid email");
        String Email = email.trim().toLowerCase();
        if (!Email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            throw new ValidationException("Invalid email");
        return Email;
    }

    
    private String validateNotBlank(String value, String field) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(field + " cannot be empty");
        }
        return value.trim();
    }
    
    private String generateTempPassword() {
        String uuidPart = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 5);
        return "T@" + uuidPart + "9a";
    }

 
    public void addEmployee() throws EmployeeDataAccessException {
        String name = null;
        int attempts = 0;
        while (attempts < MAX_INPUT_ATTEMPTS) {
            try {
                System.out.print("Enter emp name: ");
                name = validateNotBlank(sc.nextLine(), "Name");
                break;
            } catch (ValidationException e) {
                attempts++;
                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
            }
        }
        if (name == null) {
        	logger.error("Failed to add employee: max input attempts reached");
            return;
        }
        String dept = null;
        attempts = 0;
        while (attempts < MAX_INPUT_ATTEMPTS) {
            try {
                System.out.print("Enter emp dept: ");
                dept = validateNotBlank(sc.nextLine(), "Department");
                break;
            } catch (ValidationException e) {
                attempts++;
                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
            }
        }
        if (dept == null) {
        	logger.error("Failed to add employee: max input attempts reached");
            return;
        }
        String address = null;
        attempts = 0;
        while (attempts < MAX_INPUT_ATTEMPTS) {
            try {
                System.out.print("Enter emp address: ");
                address = validateNotBlank(sc.nextLine(), "Address");
                break;
            } catch (ValidationException e) {
                attempts++;
                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
            }
        }
        if (address == null) {
        	logger.error("Failed to add employee: max input attempts reached");
        	return;
        }
        String email = null;
        attempts = 0;
        while (attempts < MAX_INPUT_ATTEMPTS) {
            try {
                System.out.print("Enter emp email: ");
                email = normalizeEmail(sc.nextLine());
                break;
            } catch (ValidationException e) {
                attempts++;
                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
            }
        }
        if (email == null) {
        	logger.error("Failed to add employee: max input attempts reached");
            return;
        }
        String role = null;
        attempts = 0;
        while (attempts < MAX_INPUT_ATTEMPTS) {
            try {
                System.out.print("Enter emp role (ADMIN/MANAGER/USER): ");
                role = normalizeRole(sc.nextLine());
                break;
            } catch (ValidationException e) {
                attempts++;
                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
            }
        }
        if (role == null) {
        	logger.error("Failed to add employee: max input attempts reached");
            return;
        }
        List<String> roles = new ArrayList<>();
        roles.add(role);
        String tempPassword = generateTempPassword();
        String empId = dao.addEmployee(name, dept, address, email, roles, PasswordUtil.sha256(tempPassword)); 
        System.out.println("Employee created successfully");
        System.out.println("Employee ID: " + empId);
        System.out.println("Temporary Password: " + tempPassword);
        logger.info("Employee added successfully with id={}", empId);

    }
 
    public void deleteEmployee() throws EmployeeNotFoundException, EmployeeDataAccessException {
        String empId = null;
        int attempts = 0;
        while (attempts < MAX_INPUT_ATTEMPTS) {
            try {
                System.out.print("Enter empId to delete: ");
                empId = normalizeAndValidateId(sc.nextLine());
                break;
            } catch (ValidationException e) {
                attempts++;
                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
            }
        }
        if (empId == null) return;
        if (empId.equals(auth.getLoggedInId())) {
            throw new ValidationException("Cannot delete your own account");
        }
        if (empId.equals(ConfigUtil.getDefaultAdminId())) {
            throw new ValidationException("Cannot delete default admin account");
        }
        dao.getEmployeeById(empId);
        dao.deleteEmployee(empId);
        logger.info("Employee deleted successfully id={}", empId);
    }
 
    public void resetPassword() throws EmployeeNotFoundException, EmployeeDataAccessException {
        String empId = null;
        int attempts = 0;
        while (attempts < MAX_INPUT_ATTEMPTS) {
            try {
                System.out.print("Enter employee id: ");
                empId = normalizeAndValidateId(sc.nextLine());
                break;
            }catch (ValidationException e) {
                attempts++;
                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
            }
        }
        if (empId == null) return;
        dao.getEmployeeById(empId);
        String tempPassword = generateTempPassword();
        dao.resetPassword(empId, PasswordUtil.sha256(tempPassword)); 
        System.out.println("Password reset successful");
        System.out.println("Employee ID: " + empId);
        System.out.println("Temporary Password: " + tempPassword);
        logger.info("Password reset successful for employeeId={}", empId);
    }
 
    public void grantRole() throws EmployeeNotFoundException, EmployeeDataAccessException {
        String empId = null;
        int attempts = 0;
        while (attempts < MAX_INPUT_ATTEMPTS) {
            try {
                System.out.print("Enter employee id: ");
                empId = normalizeAndValidateId(sc.nextLine());
                break;
            }catch (ValidationException e) {
                attempts++;
                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
            }
        }
        if (empId == null) return;
        Employee emp = dao.getEmployeeById(empId);
        System.out.println("Current roles: " + emp.getRole());
        String role = null;
        attempts = 0;
        while (attempts < MAX_INPUT_ATTEMPTS) {
            try {
                System.out.print("Enter role to grant (ADMIN/MANAGER/USER): ");
                role = normalizeRole(sc.nextLine());
                break;
            } catch (ValidationException e){
                attempts++;
                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
            }
        }
        if (role == null) return;
        if (empId.equals(ConfigUtil.getDefaultAdminId()) && role.equals("ADMIN")) {
            throw new ValidationException("Default admin already has this role");
        }
        dao.grantRole(empId, role);
        logger.info("Role {} granted to employeeId={}", role, empId);
    }
 
    public void revokeRole() throws EmployeeNotFoundException, EmployeeDataAccessException {
        String empId = null;
        int attempts = 0;
        while (attempts < MAX_INPUT_ATTEMPTS) {
            try {
                System.out.print("Enter employee id: ");
                empId = normalizeAndValidateId(sc.nextLine());
                break;
            } catch (ValidationException e) {
                attempts++;
                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
            }
        }
        if (empId == null) return;
        if (empId.equals(ConfigUtil.getDefaultAdminId())) {
            throw new ValidationException("Cannot modify default admin's roles");
        }
        Employee emp = dao.getEmployeeById(empId);
        System.out.println("Current roles: " + emp.getRole());
        String role = null;
        attempts = 0;
        while (attempts < MAX_INPUT_ATTEMPTS) {
            try {
                System.out.print("Enter role to revoke (ADMIN/MANAGER/USER): ");
                role = normalizeRole(sc.nextLine());
                break;
            } catch (ValidationException e) {
                attempts++;
                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
            }
        }
        if (role == null) return;
        if (empId.equals(auth.getLoggedInId()) && role.equals("ADMIN")) {
            throw new ValidationException("Cannot revoke your own ADMIN role");
        }
        dao.revokeRole(empId, role);
        logger.info("Role {} revoked from employeeId={}", role, empId);
    }
 
    public void viewAll() throws EmployeeDataAccessException {
        List<Employee> employees = dao.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees found");
            return;
        }
        for (Employee emp : employees) {
            System.out.println(emp);
        }
    }
 
    public void viewById(String id) throws EmployeeNotFoundException, EmployeeDataAccessException {
        String empId = normalizeAndValidateId(id);
        Employee emp = dao.getEmployeeById(empId);
        System.out.println(emp);
    }

    public void viewEmployees() throws EmployeeNotFoundException, EmployeeDataAccessException {
        System.out.print("Enter ID or ALL: ");
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase("ALL")) {
            viewAll();
        } else {
            viewById(input);
        }
    }

    public void updateEmployee(RoleOptions role, String loggedInId)
            throws EmployeeNotFoundException, EmployeeDataAccessException {
        String empId;
        if (role == RoleOptions.USER) {
            empId = normalizeAndValidateId(loggedInId);
        } else {
            int attempts = 0;
            empId = null;
            while (attempts < MAX_INPUT_ATTEMPTS) {
                try {
                    System.out.print("Enter employee id: ");
                    empId = normalizeAndValidateId(sc.nextLine());
                    break;
                } catch (ValidationException e) {
                    attempts++;
                    logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
                }
            }
            if (empId == null) return;
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
                int attempts;
                switch (option) {
                    case NAME:
                        if (!isPrivileged)
                            throw new ValidationException("Only ADMIN or MANAGER can update name");
                        attempts = 0;
                        while (attempts < MAX_INPUT_ATTEMPTS) {
                            try {
                                System.out.print("Enter new name: ");
                                emp.setName(validateNotBlank(sc.nextLine(), "Name"));
                                break;
                            } catch (ValidationException e) {
                                attempts++;
                                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
                            }
                        }
                        break;
                    case DEPARTMENT:
                        if (!isPrivileged)
                            throw new ValidationException("Only ADMIN or MANAGER can update department");
                        attempts = 0;
                        while (attempts < MAX_INPUT_ATTEMPTS) {
                            try {
                                System.out.print("Enter new department: ");
                                emp.setDepartment(validateNotBlank(sc.nextLine(), "Department"));
                                break;
                            } catch (ValidationException e) {
                                attempts++;
                                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
                            }
                        }
                        break;
                    case ADDRESS:
                        attempts = 0;
                        while (attempts < MAX_INPUT_ATTEMPTS) {
                            try {
                                System.out.print("Enter new address: ");
                                emp.setAddress(validateNotBlank(sc.nextLine(), "Address"));
                                break;
                            } catch (ValidationException e) {
                                attempts++;
                                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
                            }
                        }
                        break;
                    case EMAIL:
                        attempts = 0;
                        while (attempts < MAX_INPUT_ATTEMPTS) {
                            try {
                                System.out.print("Enter new email: ");
                                emp.setEmail(normalizeEmail(sc.nextLine()));
                                break;
                            } catch (ValidationException e) {
                                attempts++;
                                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
                            }
                        }
                        break;
                    case ALL:
                        if (!isPrivileged)
                            throw new ValidationException("Only ADMIN or MANAGER can update all fields");
                        attempts = 0;
                        while (attempts < MAX_INPUT_ATTEMPTS) {
                            try {
                                System.out.print("Enter new name: ");
                                emp.setName(validateNotBlank(sc.nextLine(), "Name"));
                                break;
                            } catch (ValidationException e) {
                                attempts++;
                                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
                            }
                        }
                        attempts = 0;
                        while (attempts < MAX_INPUT_ATTEMPTS) {
                            try {
                                System.out.print("Enter new department: ");
                                emp.setDepartment(validateNotBlank(sc.nextLine(), "Department"));
                                break;
                            } catch (ValidationException e) {
                                attempts++;
                                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
                            }
                        }
                        attempts = 0;
                        while (attempts < MAX_INPUT_ATTEMPTS) {
                            try {
                                System.out.print("Enter new address: ");
                                emp.setAddress(validateNotBlank(sc.nextLine(), "Address"));
                                break;
                            } catch (ValidationException e) {
                                attempts++;
                                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
                            }
                        }
                        attempts = 0;
                        while (attempts < MAX_INPUT_ATTEMPTS) {
                            try {
                                System.out.print("Enter new email: ");
                                emp.setEmail(normalizeEmail(sc.nextLine()));
                                break;
                            } catch (ValidationException e) {
                                attempts++;
                                logger.warn("{} (Attempt {}/{})", e.getMessage(), attempts, MAX_INPUT_ATTEMPTS);
                            }
                        }
                        break;
				default:
					break;
                }
                dao.updateEmployee(emp.getId(), emp.getName(), emp.getDepartment(), emp.getAddress(), emp.getEmail());
                logger.info("Employee updated successfully id={}", emp.getId());
            } catch (ValidationException e) {
            	logger.warn("Update failed: {}", e.getMessage());
            }
        }
    }
}
