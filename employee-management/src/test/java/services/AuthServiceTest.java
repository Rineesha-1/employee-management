package services;

import dao.EmployeeDAO;
import empUtil.PasswordUtil;
import enums.RoleOptions;
import exceptions.EmployeeNotFoundException;
import exceptions.ValidationException;
import model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows; 
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

class AuthServiceTest {

	private EmployeeDAO dao;
	private Scanner sc;
	private AuthService auth;

	@BeforeEach
	void setup() {
		dao = mock(EmployeeDAO.class);
		sc = mock(Scanner.class);
		auth = new AuthService(dao, sc);
	}

	@Test
	void login_validCredentials_shouldSucceed() throws Exception {
		Employee emp = new Employee();
		emp.setId("tek1");
		emp.setPassword(PasswordUtil.sha256("pass"));
		emp.setRole(List.of("ADMIN"));
		when(sc.nextLine()).thenReturn("tek1", "pass");
		when(dao.getEmployeeById("tek1")).thenReturn(emp);
		auth.login();
		assertEquals("tek1", auth.getLoggedInId());
		assertEquals(RoleOptions.ADMIN, auth.getLoggedInRole());
	} 
	@Test
	void login_emptyId_shouldFail() {
		when(sc.nextLine()).thenReturn("", "").thenReturn("", "").thenReturn("", "");
		assertThrows(ValidationException.class, () -> auth.login());
	}

	@Test
	void login_emptyPassword_shouldFail() {
		when(sc.nextLine()).thenReturn("tek1", "").thenReturn("tek2", "").thenReturn("tek1", "");
		assertThrows(ValidationException.class, () -> auth.login());
	} 
	@Test
	void login_invalidPassword_shouldFail() throws Exception {
		Employee emp = new Employee();
		emp.setId("tek1");
		emp.setPassword(PasswordUtil.sha256("correct"));
		when(sc.nextLine()).thenReturn("tek1", "pass12", "tek1", "pass1", "tek1", "pass");
		when(dao.getEmployeeById("tek1")).thenReturn(emp);
		assertThrows(ValidationException.class, () -> auth.login());
	} 
	@Test
	void login_employeeNotFound_shouldFail() throws Exception {
		when(sc.nextLine()).thenReturn("tek1", "pass", "tek1", "pass", "tek1", "pass");
		when(dao.getEmployeeById("tek1")).thenThrow(new EmployeeNotFoundException("Not found"));
		assertThrows(ValidationException.class, () -> auth.login());
	}
	@Test
	void logout_shouldClearSession() {
	    auth.logout();
	    assertEquals(null, auth.getLoggedInId());
	}
	@Test
	void changePassword_validInput_shouldSucceed() throws Exception {
		Employee emp = new Employee();
		emp.setId("tek1");
		emp.setPassword(PasswordUtil.sha256("old"));
		when(sc.nextLine()).thenReturn("tek1", "old");
		when(dao.getEmployeeById("tek1")).thenReturn(emp);
		auth.login();
		when(sc.nextLine()).thenReturn("NewPass@123", "NewPass@123");
		auth.changePassword();
		verify(dao).changePassword(eq("tek1"), any());
	}

	@Test
	void changePassword_mismatch_shouldFail() throws Exception {
		Employee emp = new Employee();
		emp.setId("tek1");
		emp.setPassword(PasswordUtil.sha256("old"));
		when(sc.nextLine()).thenReturn("tek1", "old");
		when(dao.getEmployeeById("tek1")).thenReturn(emp);
		auth.login();
		when(sc.nextLine()).thenReturn("NewPass@123", "WrongPass@123");
		assertThrows(ValidationException.class, () -> auth.changePassword());
	}

	@Test
	void changePassword_samePassword_shouldFail() throws Exception {
		Employee emp = new Employee();
		emp.setId("tek1");
		emp.setPassword(PasswordUtil.sha256("same"));
		when(sc.nextLine()).thenReturn("tek1", "same");
		when(dao.getEmployeeById("tek1")).thenReturn(emp);
		auth.login();
		when(sc.nextLine()).thenReturn("same", "same");
		assertThrows(ValidationException.class, () -> auth.changePassword());
	}
}
