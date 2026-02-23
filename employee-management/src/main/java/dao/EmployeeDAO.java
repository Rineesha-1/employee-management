package dao;

import java.util.List; 
import exceptions.EmployeeDataAccessException;
import exceptions.EmployeeNotFoundException;
import model.Employee;

public interface EmployeeDAO {
	String addEmployee(String name, String dept, String address, String email, List<String> role, String hashPassword) throws EmployeeDataAccessException;
	void updateEmployee(String id, String name, String dept, String address, String email) throws EmployeeNotFoundException, EmployeeDataAccessException;
	void deleteEmployee(String id) throws EmployeeNotFoundException, EmployeeDataAccessException;
	void changePassword(String id, String password) throws EmployeeNotFoundException, EmployeeDataAccessException;
	void resetPassword(String id, String password) throws EmployeeNotFoundException, EmployeeDataAccessException;
	void grantRole(String id, String role) throws EmployeeNotFoundException, EmployeeDataAccessException;
	void revokeRole(String id, String role) throws EmployeeNotFoundException, EmployeeDataAccessException;
	Employee getEmployeeById(String id) throws EmployeeNotFoundException, EmployeeDataAccessException;
	List<Employee> getAllEmployees() throws EmployeeDataAccessException;
}