package services;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dao.EmployeeDAO;
import dao.EmployeeJsonDAOImpl;
import empUtil.PasswordUtil;
import enums.RoleOptions;
import exceptions.EmployeeDataAccessException;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidDataException;
import model.Employee;

class AuthServiceTest {

    private EmployeeDAO dao;
    @BeforeEach
    void setup() {
        dao = new EmployeeJsonDAOImpl();
    }
    @Test
    void login_success() throws EmployeeDataAccessException { 
        String password = "Pass@123";
        String hashed = PasswordUtil.sha256(password);
        String empId = dao.addEmployee("Test User","IT","Test Address","test@test.com",List.of("ADMIN"),hashed);
        String input = empId + "\n" + password + "\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes()));

        AuthService authService = new AuthService(dao, sc);

        // Act
        authService.login();

        // Assert
        assertEquals(empId, authService.getLoggedInId());
        assertEquals(RoleOptions.ADMIN, authService.getLoggedInRole());
        assertTrue(authService.isFirstLogin());
    }

    @Test
    void login_invalidPassword_thenSuccess() throws EmployeeDataAccessException {
        // Arrange
        String correctPassword = "Pass@123";
        String wrongPassword = "Wrong@123";

        String empId = dao.addEmployee(
                "User2",
                "IT",
                "Addr",
                "u2@test.com",
                List.of("USER"),
                PasswordUtil.sha256(correctPassword)
        );

        // first attempt wrong, second correct
        String input =
                empId + "\n" + wrongPassword + "\n" +
                empId + "\n" + correctPassword + "\n";

        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes()));
        AuthService authService = new AuthService(dao, sc);

        // Act
        authService.login();

        // Assert
        assertEquals(empId, authService.getLoggedInId());
        assertEquals(RoleOptions.USER, authService.getLoggedInRole());
    }

    @Test
    void changePassword_success() throws InvalidDataException, EmployeeNotFoundException, EmployeeDataAccessException {
        // Arrange
        String oldPassword = "Old@1234";
        String newPassword = "New@1234";

        String empId = dao.addEmployee(
                "User3",
                "IT",
                "Addr",
                "u3@test.com",
                List.of("USER"),
                PasswordUtil.sha256(oldPassword)
        );

        // login + change password inputs
        String input =
                empId + "\n" + oldPassword + "\n" +
                newPassword + "\n" + newPassword + "\n";

        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes()));
        AuthService authService = new AuthService(dao, sc);

        // Act
        authService.login();
        authService.changePassword();

        // Assert
        Employee emp = dao.getEmployeeById(empId);
        assertEquals(PasswordUtil.sha256(newPassword), emp.getPassword());
        assertFalse(emp.isFirstLogin());
    }

    @Test
    void changePassword_sameAsOld_throwsException() throws EmployeeDataAccessException {
        // Arrange
        String password = "Same@123";

        String empId = dao.addEmployee(
                "User4",
                "IT",
                "Addr",
                "u4@test.com",
                List.of("USER"),
                PasswordUtil.sha256(password)
        );

        String input =
                empId + "\n" + password + "\n" +
                password + "\n" + password + "\n";

        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes()));
        AuthService authService = new AuthService(dao, sc);

        authService.login();

        // Assert
        assertThrows(Exception.class, () -> authService.changePassword());
    }
}
