package modelconstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.LoginRoleChoice;

public class Employee {
    private String id;
    private String name;
    private String dept;
    private String address;
    private String email;
    private String role;
    private String password;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDept() { return dept; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getPassword() { return password; }

    public void setId(String id) {
        Pattern idPattern = Pattern.compile("tek\\d+"); 
        Matcher matcher = idPattern.matcher(id);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid Id");
        }
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid name");
        }
        this.name = name;
    }

    public void setDept(String dept) {
        if (dept == null || dept.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid dept");
        }
        this.dept = dept;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid address");
        }
        this.address = address;
    }

    public void setEmail(String email) {
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        Matcher matcher = emailPattern.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid Email");
        }
        this.email = email;
    }

    public void setRole(String role) {
        try {
            LoginRoleChoice.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Role");
        }
        this.role = role.toUpperCase();
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid password");
        }
        this.password = password;
    }
}