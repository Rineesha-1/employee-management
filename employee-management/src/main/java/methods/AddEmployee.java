package methods;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import controller.Input;
import modelconstructor.Employee;

public class AddEmployee {
    @SuppressWarnings("unchecked")
    public void insert() {
        ViewEmployee getEmployee = new ViewEmployee();
        AutoId autoIDGenerator = new AutoId();
        Employee employee = new Employee();
        try {
            System.out.print("Enter emp name: ");
            String name = Input.SC.nextLine();
            employee.setName(name);

            System.out.print("Enter emp dept: ");
            String dept = Input.SC.nextLine();
            employee.setDept(dept);

            System.out.print("Enter emp address: ");
            String address = Input.SC.nextLine();
            employee.setAddress(address);

            System.out.print("Enter emp email: ");
            String email = Input.SC.nextLine();
            employee.setEmail(email);

            System.out.print("Enter emp role: ");
            String role = Input.SC.nextLine().trim();
            employee.setRole(role);

            JSONArray rolesArray = new JSONArray();
            rolesArray.add(role.toUpperCase());

            String password = ResetPassword.defaultPass;
            employee.setPassword(password);

            String hashPassword = Password.hash(password);

            JSONParser parser = new JSONParser();
            JSONArray array = new JSONArray();

            if (getEmployee.file.exists() && getEmployee.file.length() > 2) {
                Object obj = parser.parse(new FileReader(getEmployee.file));
                array = (JSONArray) obj;
            }
            String id = autoIDGenerator.genId();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("name", name);
            jsonObject.put("department", dept);
            jsonObject.put("address", address);
            jsonObject.put("email", email);
            jsonObject.put("role", rolesArray);
            jsonObject.put("password", hashPassword);
            array.add(jsonObject);
            FileWriter fw = new FileWriter(getEmployee.file);
            fw.write(array.toJSONString());
            fw.close();
            System.out.println("Employee inserted successfully\n");
            getEmployee.view_all();
        } catch (ParseException e) {
            System.out.println("Parser error");
        } catch (IOException e) {
            System.out.println("Error writing to the file");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}