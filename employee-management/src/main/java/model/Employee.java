package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Employee {
	private String id;
	private String name;
	private String department;
	private String address;
	private String email;
	private List<String> role;
	private String password;
	private boolean firstLogin = true;

	public Employee() {
		role = new ArrayList<>();
	}
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
	public boolean isFirstLogin() {
		return firstLogin;
	}
	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}
	public void setId(String id) {
		this.id = id.toLowerCase();
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setRole(List<String> role) {
		this.role.clear();
		if (role != null) {
			this.role.addAll(role);
		}
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean hasRole(String roleName) {
		if (roleName == null) {
			return false;
		}
		for (String r : role) {
			if (r.equalsIgnoreCase(roleName)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Employee))
			return false;
		Employee other = (Employee) obj;
		return Objects.equals(id, other.id);
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public String toString() {
		return "Emp ID: " + id + " | Name: " + name + " | Department: " + department + " | Address: " + address+ " | Email: " + email + " | Roles: " + role;
	}

}