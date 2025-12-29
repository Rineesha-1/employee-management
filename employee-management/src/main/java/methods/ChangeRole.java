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

public class ChangeRole {

    @SuppressWarnings("unchecked")
    public void grantRole() {
        JSONParser parser = new JSONParser();
        Employee employee = new Employee();
        ViewEmployee getEmployee = new ViewEmployee();

        if (!getEmployee.file.exists() || getEmployee.file.length() <= 2) {
            System.out.println("No employees\n");
            return;
        }
        try {
            Object empData = parser.parse(new FileReader(getEmployee.file));
            JSONArray array = (JSONArray) empData;

            System.out.print("Enter emp id: ");
            String id = Input.SC.nextLine().trim();
            employee.setId(id);

            System.out.print("Enter new role: ");
            String role = Input.SC.nextLine().trim().toUpperCase();
            employee.setRole(role);

            boolean found = false;

            for (Object obj : array) {
                JSONObject jsonObject = (JSONObject) obj;
                String currId = (String) jsonObject.get("id");

                if (currId.equals(id)) {
                    found = true;
                    JSONArray roleArray = (JSONArray) jsonObject.get("role");
                    if (!roleArray.contains(role)) {
                        roleArray.add(role);
                        System.out.println("Employee updated role\n");
                    } else {
                        System.out.println("Cannot assign same role again\n");
                    }
                    break;
                }
            }
            if (found) {
                FileWriter fw = new FileWriter(getEmployee.file);
                fw.write(array.toJSONString());
                fw.flush();
                fw.close();
            } else {
                System.out.println("Employee not found\n");
            }
        } catch (ParseException e) {
            System.out.println("Parser error");
        } catch (IOException e) {
            System.out.println("Error writing to the file");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
    public void revokeRole() {
        JSONParser parser = new JSONParser();
        Employee employee = new Employee();
        ViewEmployee getEmployee = new ViewEmployee();

        if (!getEmployee.file.exists() || getEmployee.file.length() <= 2) {
            System.out.println("No employees\n");
            return;
        }
        try {
            Object empData = parser.parse(new FileReader(getEmployee.file));
            JSONArray array = (JSONArray) empData;

            System.out.print("Enter emp id: ");
            String id = Input.SC.nextLine().trim();
            employee.setId(id);

            System.out.print("Enter role to revoke: ");
            String role = Input.SC.nextLine().trim().toUpperCase();
            employee.setRole(role);

            boolean found = false;

            for (Object obj : array) {
                JSONObject jsonObject = (JSONObject) obj;
                String currId = (String) jsonObject.get("id");

                if (currId.equals(id)) {
                    found = true;
                    JSONArray roleArray = (JSONArray) jsonObject.get("role");

                    if (roleArray.contains(role)) {
                        roleArray.remove(role);
                        System.out.println("Revoked employee role\n");
                    } else {
                        System.out.println("Role doesn't exist\n");
                    }
                    break;
                }
            }

            if (found) {
                FileWriter fw = new FileWriter(getEmployee.file);
                fw.write(array.toJSONString());
                fw.flush();
                fw.close();
            } else {
                System.out.println("Employee not found\n");
            }

        } catch (ParseException e) {
            System.out.println("Parser error");
        } catch (IOException e) {
            System.out.println("Error writing to the file");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}