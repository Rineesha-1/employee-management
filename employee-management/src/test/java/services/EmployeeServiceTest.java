package services;

import dao.EmployeeDAO;
import exceptions.ValidationException;
import model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

	private EmployeeDAO dao;
	private Scanner sc;
	private AuthService auth;
	private EmployeeService service;

	@BeforeEach
	void setup() {
		dao = mock(EmployeeDAO.class);
		sc = mock(Scanner.class);
		auth = mock(AuthService.class);
		service = new EmployeeService(dao, sc, auth);
	}

	@Test
	void addEmployee_validInput_shouldCallDao() throws Exception {
		when(sc.nextLine()).thenReturn("John", "IT", "Chennai", "john@test.com", "USER");
		when(dao.addEmployee(any(), any(), any(), any(), any(), any())).thenReturn("tek1");
		service.addEmployee();
		verify(dao).addEmployee(any(), any(), any(), any(), any(), any());
	}

	@Test
	void addEmployee_invalidEmail_shouldFail() {
		when(sc.nextLine()).thenReturn("John", "IT", "Chennai", "bademail", "USER");
		assertDoesNotThrow(() -> service.addEmployee());
	}

	@Test
	void deleteEmployee_valid_shouldCallDao() throws Exception {
		Employee emp = new Employee();
		emp.setId("tek2");
		when(sc.nextLine()).thenReturn("tek2");
		when(auth.getLoggedInId()).thenReturn("tek1");
		when(dao.getEmployeeById("tek2")).thenReturn(emp);
		service.deleteEmployee();
		verify(dao).deleteEmployee("tek2");
	}

	@Test
	void deleteEmployee_self_shouldFail() {
		when(sc.nextLine()).thenReturn("tek1");
		when(auth.getLoggedInId()).thenReturn("tek1");
		assertThrows(ValidationException.class, () -> service.deleteEmployee());
	}

	@Test
	void resetPassword_valid_shouldCallDao() throws Exception {
		Employee emp = new Employee();
		emp.setId("tek2");
		when(sc.nextLine()).thenReturn("tek2");
		when(dao.getEmployeeById("tek2")).thenReturn(emp);
		service.resetPassword();
		verify(dao).resetPassword(eq("tek2"), any());
	}

	@Test
	void grantRole_valid_shouldCallDao() throws Exception {
		Employee emp = new Employee();
		emp.setId("tek2");
		emp.setRole(List.of("USER"));
		when(sc.nextLine()).thenReturn("tek2", "ADMIN");
		when(dao.getEmployeeById("tek2")).thenReturn(emp);
		service.grantRole();
		verify(dao).grantRole("tek2", "ADMIN");
	}

	@Test
	void revokeRole_valid_shouldCallDao() throws Exception {
		Employee emp = new Employee();
		emp.setId("tek2");
		emp.setRole(List.of("ADMIN", "USER"));
		when(sc.nextLine()).thenReturn("tek2", "USER");
		when(auth.getLoggedInId()).thenReturn("tek1");
		when(dao.getEmployeeById("tek2")).thenReturn(emp);
		service.revokeRole();
		verify(dao).revokeRole("tek2", "USER");
	}

	@Test
	void revokeRole_ownAdmin_shouldFail() {
		when(sc.nextLine()).thenReturn("tek1", "ADMIN");
		when(auth.getLoggedInId()).thenReturn("tek1");
		assertThrows(ValidationException.class, () -> service.revokeRole());
	}
}
