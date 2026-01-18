package dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import empUtil.PasswordUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import empUtil.ConfigUtil;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidDataException;
import model.Employee;

public class EmployeeJsonDAOImpl implements EmployeeDAO {

	private static final File FILE = new File("employees.json");
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private final List<Employee> employees = new ArrayList<>();
	private int lastId = 0;

	public EmployeeJsonDAOImpl() {
		load();
		ensureDefaultAdmin();
		save();
	}

	private void load() {
		employees.clear();
		lastId = 0;
		if (!FILE.exists() || FILE.length() == 0)
			return;
		try {
			List<Employee> list = MAPPER.readValue(FILE, new TypeReference<List<Employee>>() {
			});
			if (list != null)
				employees.addAll(list);
			for (Employee e : employees) {
				lastId = Math.max(lastId, extractNumber(e.getId()));
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to load employees.json", e);
		}
	}

	private void save() {
		try {
			MAPPER.writerWithDefaultPrettyPrinter().writeValue(FILE, employees);
		} catch (IOException e) {
			throw new RuntimeException("Failed to save employees.json", e);
		}
	}

	private int extractNumber(String id) {
		try {
			if (id == null)
				return 0;
			String s = id.trim().toLowerCase();
			if (!s.startsWith("tek"))
				return 0;
			return Integer.parseInt(s.substring(3));
		} catch (Exception e) {
			return 0;
		}
	}

	private String nextId() {
		lastId++;
		return "tek" + lastId;
	}

	private Employee findByIdInternal(String id) {
		if (id == null)
			return null;
		String key = id.trim().toLowerCase();
		for (Employee e : employees) {
			if (e.getId() != null && e.getId().equalsIgnoreCase(key))
				return e;
		}
		return null;
	}

	private Employee findByEmailInternal(String email) {
		if (email == null)
			return null;
		String key = email.trim().toLowerCase();
		for (Employee e : employees) {
			if (e.getEmail() != null && e.getEmail().equalsIgnoreCase(key))
				return e;
		}
		return null;
	}

	private void ensureDefaultAdmin() {
		String adminId = ConfigUtil.getDefaultAdminId();
		String adminName = ConfigUtil.getDefaultAdminName();
		String adminEmail = ConfigUtil.getDefaultAdminEmail();
		String adminPassword = ConfigUtil.getDefaultAdminPassword();
		String adminDept = ConfigUtil.getDefaultAdminDepartment();
		String adminAddress = ConfigUtil.getDefaultAdminAddress();
		Employee admin = findByIdInternal(adminId);
		if (admin == null)
			admin = findByEmailInternal(adminEmail);
		if (admin == null) {
			admin = new Employee();
			admin.setId(adminId);
			admin.setName(adminName);
			admin.setDepartment(adminDept);
			admin.setAddress(adminAddress);
			admin.setEmail(adminEmail);
			List<String> roles = new ArrayList<>();
			roles.add("ADMIN");
			admin.setRole(roles);
			admin.setPassword(PasswordUtil.sha256(adminPassword));
			admin.setFirstLogin(true);
			employees.add(admin);
		} else {
			List<String> roles = new ArrayList<>(admin.getRole());
			boolean hasAdmin = false;
			for (String r : roles) {
				if ("ADMIN".equalsIgnoreCase(r)) {
					hasAdmin = true;
					break;
				}
			}
			if (!hasAdmin) {
				roles.add("ADMIN");
				admin.setRole(roles);
			}
			if (admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
				admin.setPassword(PasswordUtil.sha256(adminPassword));
			}
			if (admin.getEmail() == null)
				admin.setEmail(adminEmail);
		}
		lastId = Math.max(lastId, extractNumber(admin.getId()));
	}

	@Override
	public String addEmployee(String name, String dept, String address, String email, List<String> role,
			String hashPassword) {
		String id = nextId();
		Employee emp = new Employee();
		emp.setId(id);
		emp.setName(name);
		emp.setDepartment(dept);
		emp.setAddress(address);
		emp.setEmail(email);
		emp.setRole(role);
		emp.setPassword(hashPassword);
		emp.setFirstLogin(true);
		employees.add(emp);
		employees.sort(Comparator.comparing(Employee::getId));
		save();
		return id;
	}

	@Override
	public void updateEmployee(String id, String name, String dept, String address, String email)
			throws EmployeeNotFoundException {
		Employee emp = findByIdInternal(id);
		if (emp == null)
			throw new EmployeeNotFoundException("Employee not found");
		emp.setName(name);
		emp.setDepartment(dept);
		emp.setAddress(address);
		emp.setEmail(email);
		save();
	}

	@Override
	public void deleteEmployee(String id) throws EmployeeNotFoundException {
		Employee emp = findByIdInternal(id);
		if (emp == null)
			throw new EmployeeNotFoundException("Employee not found");
		employees.remove(emp);
		save();
	}

	@Override
	public void changePassword(String id, String password) throws EmployeeNotFoundException {
		Employee emp = findByIdInternal(id);
		if (emp == null)
			throw new EmployeeNotFoundException("Employee not found");
		emp.setPassword(password);
		emp.setFirstLogin(false);
		save();
	}

	@Override
	public void resetPassword(String id, String password) throws EmployeeNotFoundException {
		Employee emp = findByIdInternal(id);
		if (emp == null)
			throw new EmployeeNotFoundException("Employee not found");

		emp.setPassword(password);
		emp.setFirstLogin(true);
		save();
	}

	@Override
	public void grantRole(String id, String role) throws InvalidDataException, EmployeeNotFoundException {
		Employee emp = findByIdInternal(id);
		if (emp == null)
			throw new EmployeeNotFoundException("Employee not found");
		for (String r : emp.getRole()) {
			if (r.equalsIgnoreCase(role))
				throw new InvalidDataException("Employee already has this role");
		}
		List<String> roles = new ArrayList<>(emp.getRole());
		roles.add(role);
		emp.setRole(roles);
		save();
	}

	@Override
	public void revokeRole(String id, String role) throws InvalidDataException, EmployeeNotFoundException {
		Employee emp = findByIdInternal(id);
		if (emp == null)
			throw new EmployeeNotFoundException("Employee not found");
		if (emp.getRole().size() == 1)
			throw new InvalidDataException("Employee must have at least one role");
		List<String> roles = new ArrayList<>(emp.getRole());
		boolean removed = roles.removeIf(r -> r.equalsIgnoreCase(role));
		if (!removed)
			throw new InvalidDataException("Role does not exist for this employee");
		if (roles.isEmpty())
			throw new InvalidDataException("Employee must have at least one role");
		emp.setRole(roles);
		save();
	}

	@Override
	public Employee getEmployeeById(String id) throws EmployeeNotFoundException {
		Employee emp = findByIdInternal(id);
		if (emp == null)
			throw new EmployeeNotFoundException("Employee not found");
		return emp;
	}

	@Override
	public List<Employee> getAllEmployees() {
		return new ArrayList<>(employees);
	}
}