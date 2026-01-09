package dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.InvalidDataException;
import model.Employee;
import services.AuthService;
import store.DataStore;

public class EmployeeJsonDAOImpl implements EmployeeDAO {
    private static final File FILE = new File("employees.json");
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final DataStore store;
    public EmployeeJsonDAOImpl(DataStore store) {
        this.store = store;
        loadToStore();
        ensureDefaultAdmin();
        saveFromStore();
    }

    private void loadToStore() {
        store.getEmployees();
        store.setLastId(0);
        if (!FILE.exists() || FILE.length() == 0)
            return;
        try {
            List<Employee> list = MAPPER.readValue(FILE, new TypeReference<List<Employee>>() {});
            int max = 0;
            for (Employee e : list) {
                store.getEmployees().put(e.getId(), e);
                max = Math.max(max, extractNumber(e.getId()));
            }
            store.setLastId(max);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load employees.json", e);
        }
    }

    private void saveFromStore() {
        try {
            List<Employee> list =new ArrayList<>(store.getEmployees().values());
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(FILE, list);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save employees.json", e);
        }
    } 
    
    private void ensureDefaultAdmin() {
        Employee admin = store.getEmployees().get("tek0");
        if (admin == null) {
            admin = new Employee();
            admin.setId("tek0");
            admin.setName("Admin");
            admin.setDepartment("ADMIN");
            admin.setAddress("System");
            admin.setEmail("admin@system.com");
            List<String> roles = new ArrayList<>();
            roles.add("ADMIN");
            admin.setRole(roles);
            admin.setFirstLogin(true);
            store.getEmployees().put("tek0", admin);
        } 
        admin.setPassword(AuthService.hash("password1"));
        saveFromStore();
    }

    private int extractNumber(String id) {
        try {
            return Integer.parseInt(id.substring(3));
        } catch (Exception e) {
            return 0;
        }
    }

    private String nextId() {
        int next = store.getLastId() + 1;
        store.setLastId(next);
        return "tek" + next;
    } 

    @Override
    public String addEmployee(String name,String dept,String address,String email,List<String> roles,String hashPassword) {
        Employee emp = new Employee();
        String id = nextId();
        emp.setId(id);
        emp.setName(name);
        emp.setDepartment(dept);
        emp.setAddress(address);
        emp.setEmail(email);
        emp.setRole(roles);
        emp.setPassword(hashPassword);
        emp.setFirstLogin(true);
        store.getEmployees().put(id, emp);
        saveFromStore();
        return id;
    }

    @Override
    public void updateEmployee(String id,String name,String dept,String address,String email) {
        Employee emp = store.getEmployees().get(id);
        if (emp == null) throw new RuntimeException("Employee not found");
        emp.setName(name);
        emp.setDepartment(dept);
        emp.setAddress(address);
        emp.setEmail(email);
        saveFromStore();
    }
    @Override
    public void deleteEmployee(String id) {
        if (store.getEmployees().remove(id) == null) throw new RuntimeException("Employee not found");
        saveFromStore();
    }
    @Override
    public void viewEmployee() {
        store.getEmployees().values().forEach(System.out::println);
    }
    @Override
    public void viewEmployeeById(String id) {
        Employee emp = store.getEmployees().get(id);
        if (emp == null)
            throw new RuntimeException("Employee not found");
        System.out.println(emp);
    } 
    @Override
    public void changePassword(String id, String password) {
        Employee emp = store.getEmployees().get(id);
        if (emp == null)
            throw new RuntimeException("Employee not found");
        emp.setPassword(password);
        saveFromStore();
    }
    @Override
    public void resetPassword(String id, String password) {
        changePassword(id, password);
    } 
    @Override
    public void grantRole(String id, String role) throws InvalidDataException {
        Employee emp = store.getEmployees().get(id);
        if (emp == null) throw new RuntimeException("Employee not found");
        if (emp.getRole().contains(role)) throw new InvalidDataException("Employee already has this role");
        List<String> roles = new ArrayList<>(emp.getRole());
        roles.add(role);
        emp.setRole(roles);
        saveFromStore();
    }
    @Override
    public void revokeRole(String id, String role) throws InvalidDataException {
        Employee emp = store.getEmployees().get(id);
        if (emp == null) throw new RuntimeException("Employee not found");
        if (emp.getRole().size() == 1) throw new InvalidDataException("Employee must have at least one role");
        List<String> roles = new ArrayList<>(emp.getRole());
        if (!roles.remove(role))
            throw new InvalidDataException("Role does not exist");
        emp.setRole(roles);
        saveFromStore();
    }
}
