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

//DAO implementation that stores employee data in JSON file
public class EmployeeJsonDAOImpl implements EmployeeDAO {
	private final File file;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private final List<Employee> employees = new ArrayList<>();
	private int lastId = 0;
//default constructor
	public EmployeeJsonDAOImpl() {
		this(new File("employees.json"));
	}
	public EmployeeJsonDAOImpl(File file) {
		this.file = file;
		load();
		ensureDefaultAdmin();
		save();
	}
//loads data into memory
	private void load() {
		employees.clear();
		lastId = 0;
		if (!file.exists() || file.length() == 0)
			return;
		try {
			List<Employee> list = MAPPER.readValue(file, new TypeReference<List<Employee>>() {
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
//saves data
	private void save() {
		try {
			MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, employees);
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
//auto generates next id 
	private String nextId() {
		lastId++;
		return "tek" + lastId;
	}
	//finds by id
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
	//finds by email
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
//checks default admin
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
	// Adds a new employee and returns generated employee ID
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
//updates employee details
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
//deletes employee details
	@Override
	public void deleteEmployee(String id) throws EmployeeNotFoundException {
		Employee emp = findByIdInternal(id);
		if (emp == null)
			throw new EmployeeNotFoundException("Employee not found");
		employees.remove(emp);
		save();
	}
//used to change self password
	@Override
	public void changePassword(String id, String password) throws EmployeeNotFoundException {
		Employee emp = findByIdInternal(id);
		if (emp == null)
			throw new EmployeeNotFoundException("Employee not found");
		emp.setPassword(password);
		emp.setFirstLogin(false);
		save();
	}
//resets individual password 
	@Override
	public void resetPassword(String id, String password) throws EmployeeNotFoundException {
		Employee emp = findByIdInternal(id);
		if (emp == null)
			throw new EmployeeNotFoundException("Employee not found");
		emp.setPassword(password);
		emp.setFirstLogin(true);
		save();
	}
//grants a new role
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
//revokes a role from employee
	@Override 
	public void revokeRole(String id, String role)
	        throws InvalidDataException, EmployeeNotFoundException {
	    Employee emp = findByIdInternal(id);
	    if (emp == null) {
	        throw new EmployeeNotFoundException("Employee not found");
	    }
	    List<String> roles = new ArrayList<>(emp.getRole()); 
	    boolean found = false;
	    for (String r : roles) {
	        if (r.equalsIgnoreCase(role)) {
	            found = true;
	            break;
	        }
	    }
	    if (!found) {
	        throw new InvalidDataException("Employee does not have this role");
	    }
	    if (roles.size() == 1) {
	        throw new InvalidDataException("Employee must have at least one role");
	    }
	    for (int i = 0; i < roles.size(); i++) {
	        if (roles.get(i).equalsIgnoreCase(role)) {
	            roles.remove(i);
	            break;
	        }
	    }
	    emp.setRole(roles);
	    save();
	}

//retrieves employee by id
	@Override
	public Employee getEmployeeById(String id) throws EmployeeNotFoundException {
		Employee emp = findByIdInternal(id);
		if (emp == null)
			throw new EmployeeNotFoundException("Employee not found");
		return emp;
	}
//returns all employees
	@Override
	public List<Employee> getAllEmployees() {
		return new ArrayList<>(employees);
	}
}