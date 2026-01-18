package dao;

import java.util.List;
import exceptions.InvalidDataException;
import exceptions.EmployeeNotFoundException;
import model.Employee;

public interface EmployeeDAO {
	String addEmployee(String name, String dept, String address, String email, List<String> role, String hashPassword);

	void updateEmployee(String id, String name, String dept, String address, String email)
			throws EmployeeNotFoundException;

	void deleteEmployee(String id) throws EmployeeNotFoundException;

	void changePassword(String id, String password) throws EmployeeNotFoundException;

	void resetPassword(String id, String password) throws EmployeeNotFoundException;

	void grantRole(String id, String role) throws InvalidDataException, EmployeeNotFoundException;

	void revokeRole(String id, String role) throws InvalidDataException, EmployeeNotFoundException;

	Employee getEmployeeById(String id) throws EmployeeNotFoundException;

	List<Employee> getAllEmployees();
}