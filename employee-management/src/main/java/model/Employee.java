package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
 
public class Employee {
    private static final Pattern ID_PATTERN = Pattern.compile("(?i)tek\\d+");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private String id;
    private String name;
    private String department;
    private String address;
    private String email;
    private List<String> role = new ArrayList<>();
    private String password;
    public Employee() {}
    public String getId() { 
    	return id;
    }
    public String getName() { 
    	return name; 
    }
    public String getDepartment() { 
    	return department;
    }
    public String getAddress() { 
    	return address; 
    }
    public String getEmail() { 
    	return email;
    }
    public List<String> getRole() { 
    	return role; 
    }
    public String getPassword() { 
    	return password;
    }
    public void setId(String id) {
        if (id == null || !ID_PATTERN.matcher(id.trim()).matches()) {
            throw new IllegalArgumentException("Invalid id");
        }
        this.id = id.toLowerCase();
    }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Invalid name");
        this.name = name;
    }
    public void setDepartment(String department) {
        if (department == null || department.trim().isEmpty()) throw new IllegalArgumentException("Invalid department");
        this.department = department;
    }
    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) throw new IllegalArgumentException("Invalid address");
        this.address = address;
    }
    public void setEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.email = email;
    }
    public void setRole(List<String> role) {
        this.role = (role == null) ? new ArrayList<>() : new ArrayList<>(role);
    }
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) throw new IllegalArgumentException("Invalid password");
        this.password = password;
    }
    public boolean hasRole(String r) {
        if (r == null) return false;
        for (String x : role) if (x != null && x.equalsIgnoreCase(r)) return true;
        return false;
    }
    @Override
    public String toString() {
        return "Emp ID: " + id + " | Name: " + name + " | Department: " + department
                + " | Address: " + address + " | Email: " + email + " | Roles: " + role;
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Employee e)) return false;
        return Objects.equals(id, e.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}