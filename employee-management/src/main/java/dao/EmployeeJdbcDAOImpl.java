package dao;

import empUtil.ConfigUtil;
import empUtil.DatabaseUtil;
import empUtil.PasswordUtil;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidDataException;
import model.Employee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//DAO implementation that uses Database
public class EmployeeJdbcDAOImpl implements EmployeeDAO {
	private Connection getConnection() throws SQLException {
		return DatabaseUtil.getConnection();  //gets database connection
	}
	public EmployeeJdbcDAOImpl() {
		try {
			ensureDefaultAdmin();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to initialize JDBC DAO", e);
		}
	}
//retrieves roles for given employee
	private List<String> getRoles(Connection conn, String empId) throws SQLException {
		List<String> roles = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement("select role from employee_roles where emp_id=?")) {
			ps.setString(1, empId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					roles.add(rs.getString("role"));
				}
			}
		}
		return roles;
	}
	// Maps database result set to Employee object
	private Employee mapEmployee(Connection conn, ResultSet rs) throws SQLException {
		Employee emp = new Employee();
		emp.setId(rs.getString("emp_id"));
		emp.setName(rs.getString("name"));
		emp.setDepartment(rs.getString("department"));
		emp.setAddress(rs.getString("address"));
		emp.setEmail(rs.getString("email"));
		emp.setPassword(rs.getString("password"));
		emp.setFirstLogin(rs.getBoolean("first_login"));
		emp.setRole(getRoles(conn, emp.getId()));
		return emp;
	}
//ensure default admin details
	private void ensureDefaultAdmin() throws SQLException {
		String adminId = ConfigUtil.getDefaultAdminId();
		String adminName = ConfigUtil.getDefaultAdminName();
		String adminEmail = ConfigUtil.getDefaultAdminEmail();
		String adminPassword = ConfigUtil.getDefaultAdminPassword();
		String adminDept = ConfigUtil.getDefaultAdminDepartment();
		String adminAddress = ConfigUtil.getDefaultAdminAddress();
		try (Connection conn = getConnection()) {
			try (PreparedStatement ps = conn.prepareStatement("select 1 from employees where emp_id=?")) {
				ps.setString(1, adminId);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next())
						return;
				}
			}
			conn.setAutoCommit(false);
			try {
				try (PreparedStatement ps = conn.prepareStatement(
						"insert into employees(emp_id,name,department,address,email) values (?,?,?,?,?)")) {
					ps.setString(1, adminId);
					ps.setString(2, adminName);
					ps.setString(3, adminDept);
					ps.setString(4, adminAddress);
					ps.setString(5, adminEmail);
					ps.executeUpdate();
				}
				try (PreparedStatement ps = conn.prepareStatement(
						"insert into employee_login(emp_id,password,first_login) values (?,?,true)")) {
					ps.setString(1, adminId);
					ps.setString(2, PasswordUtil.sha256(adminPassword));
					ps.executeUpdate();
				}
				try (PreparedStatement ps = conn
						.prepareStatement("insert into employee_roles(emp_id,role) values (?,?)")) {
					ps.setString(1, adminId);
					ps.setString(2, "ADMIN");
					ps.executeUpdate();
				}
				conn.commit();
				System.out.println("Default admin created (" + adminId + " / " + adminPassword + ")");
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			} finally {
				conn.setAutoCommit(true);
			}
		}
	}

	@Override
	public String addEmployee(String name, String dept, String address, String email, List<String> roles,
			String hashPassword) {
		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			int generatedId;
			try (PreparedStatement ps = conn.prepareStatement(
					"insert into employees(name,department,address,email) values (?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS)) {
				ps.setString(1, name);
				ps.setString(2, dept);
				ps.setString(3, address);
				ps.setString(4, email);
				ps.executeUpdate();
				try (ResultSet keys = ps.getGeneratedKeys()) {
					if (!keys.next())
						throw new SQLException("Failed to generate employee id");
					generatedId = keys.getInt(1);
				}
			}
			String empId = "tek" + generatedId;
			try (PreparedStatement ps = conn.prepareStatement("update employees set emp_id=? where id=?")) {
				ps.setString(1, empId);
				ps.setInt(2, generatedId);
				ps.executeUpdate();
			}
			try (PreparedStatement ps = conn
					.prepareStatement("insert into employee_login(emp_id,password,first_login) values (?,?,true)")) {
				ps.setString(1, empId);
				ps.setString(2, hashPassword);
				ps.executeUpdate();
			}
			if (roles != null && !roles.isEmpty()) {
				try (PreparedStatement ps = conn
						.prepareStatement("insert into employee_roles(emp_id,role) values (?,?)")) {
					for (String r : roles) {
						ps.setString(1, empId);
						ps.setString(2, r);
						ps.addBatch();
					}
					ps.executeBatch();
				}
			}
			conn.commit();
			return empId;
		} catch (SQLException e) {
			throw new RuntimeException("Add employee failed", e);
		}
	}

	@Override
	public void updateEmployee(String id, String name, String dept, String address, String email)
			throws EmployeeNotFoundException {
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"update employees set name=?,department=?,address=?,email=? where emp_id=?")) {
			ps.setString(1, name);
			ps.setString(2, dept);
			ps.setString(3, address);
			ps.setString(4, email);
			ps.setString(5, id);
			if (ps.executeUpdate() == 0)
				throw new EmployeeNotFoundException("Employee not found");
		} catch (SQLException e) {
			throw new RuntimeException("Update failed", e);
		}
	}

	@Override
	public void deleteEmployee(String id) throws EmployeeNotFoundException {
		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			try (PreparedStatement ps = conn.prepareStatement("delete from employee_roles where emp_id=?")) {
				ps.setString(1, id);
				ps.executeUpdate();
			}
			try (PreparedStatement ps = conn.prepareStatement("delete from employee_login where emp_id=?")) {
				ps.setString(1, id);
				ps.executeUpdate();
			}
			int deleted;
			try (PreparedStatement ps = conn.prepareStatement("delete from employees where emp_id=?")) {
				ps.setString(1, id);
				deleted = ps.executeUpdate();
			}
			if (deleted == 0) {
				conn.rollback();
				throw new EmployeeNotFoundException("Employee not found");
			}
			conn.commit();
		} catch (SQLException e) {
			throw new RuntimeException("Delete failed", e);
		}
	}

	@Override
	public void changePassword(String id, String password) throws EmployeeNotFoundException {
		try (Connection conn = getConnection();
				PreparedStatement ps = conn
						.prepareStatement("update employee_login set password=?, first_login=false where emp_id=?")) {
			ps.setString(1, password);
			ps.setString(2, id);
			if (ps.executeUpdate() == 0)
				throw new EmployeeNotFoundException("Employee not found");
		} catch (SQLException e) {
			throw new RuntimeException("Password change failed", e);
		}
	}

	@Override
	public void resetPassword(String id, String password) throws EmployeeNotFoundException {
		try (Connection conn = getConnection();
				PreparedStatement ps = conn
						.prepareStatement("update employee_login set password=?, first_login=true where emp_id=?")) {
			ps.setString(1, password);
			ps.setString(2, id);
			if (ps.executeUpdate() == 0)
				throw new EmployeeNotFoundException("Employee not found");
		} catch (SQLException e) {
			throw new RuntimeException("Password reset failed", e);
		}
	}

	@Override
	public void grantRole(String id, String role) throws InvalidDataException, EmployeeNotFoundException {
		getEmployeeById(id); 
		try (Connection conn = getConnection()) {
			try (PreparedStatement check = conn
					.prepareStatement("select 1 from employee_roles where emp_id=? and role=?")) {
				check.setString(1, id);
				check.setString(2, role);
				try (ResultSet rs = check.executeQuery()) {
					if (rs.next())
						throw new InvalidDataException("Employee already has this role");
				}
			}
			try (PreparedStatement ps = conn.prepareStatement("insert into employee_roles(emp_id,role) values (?,?)")) {
				ps.setString(1, id);
				ps.setString(2, role);
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Grant role failed", e);
		}
	}
	@Override
	public void revokeRole(String id, String role)
	        throws InvalidDataException, EmployeeNotFoundException {
	    getEmployeeById(id);
	    try (Connection conn = getConnection()) { 
	        boolean exists = false;
	        try (PreparedStatement ps = conn.prepareStatement(
	                "select 1 from employee_roles where emp_id=? and role=?")) {
	            ps.setString(1, id);
	            ps.setString(2, role);
	            try (ResultSet rs = ps.executeQuery()) {
	                exists = rs.next();
	            }
	        }
	        if (!exists) {
	            throw new InvalidDataException("Employee does not have this role");
	        } 
	        int count;
	        try (PreparedStatement ps = conn.prepareStatement(
	                "select count(*) from employee_roles where emp_id=?")) {
	            ps.setString(1, id);
	            try (ResultSet rs = ps.executeQuery()) {
	                rs.next();
	                count = rs.getInt(1);
	            }
	        } 
	        if (count == 1) {
	            throw new InvalidDataException("Employee must have at least one role");
	        } 
	        try (PreparedStatement ps = conn.prepareStatement(
	                "delete from employee_roles where emp_id=? and role=?")) {
	            ps.setString(1, id);
	            ps.setString(2, role);
	            ps.executeUpdate();
	        }
	    } catch (SQLException e) {
	        throw new RuntimeException("Revoke role failed", e);
	    }
	}

	@Override
	public Employee getEmployeeById(String id) throws EmployeeNotFoundException {
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement("""
						select e.emp_id, e.name, e.department, e.address, e.email, l.password, l.first_login from employees e join employee_login l on e.emp_id = l.emp_id where e.emp_id = ?""")) {
			ps.setString(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next())
					throw new EmployeeNotFoundException("Employee not found");
				return mapEmployee(conn, rs);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Fetch employee failed", e);
		}
	}

	@Override
	public List<Employee> getAllEmployees() {
		List<Employee> list = new ArrayList<>();
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement("""
						select e.emp_id, e.name, e.department, e.address, e.email, l.password, l.first_login from employees e join employee_login l on e.emp_id = l.emp_id order by e.emp_id""")) {
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapEmployee(conn, rs));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Fetch all employees failed", e);
		}
		return list;
	}
}
