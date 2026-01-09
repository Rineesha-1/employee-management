package dao;

import java.util.List;

import exceptions.InvalidDataException;

public interface EmployeeDAO {

    String addEmployee(String name,String dept,String address,String email,List<String> role,String hashPassword);
    void updateEmployee(String id, String name, String dept, String address, String email);
    void deleteEmployee(String id);
    void changePassword(String id, String password);
    void resetPassword(String id, String password);
    void grantRole(String id, String role) throws InvalidDataException;
    void revokeRole(String id, String role) throws InvalidDataException;
    void viewEmployeeById(String id);
    void viewEmployee();
}
