package methods;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import controller.Input;
import exceptions.EmployeeNotFoundException;

public class ViewEmployee {

    JSONParser parser = new JSONParser(); 
    public final File file = new File("employees.json");

    public void view_all() {
        if (!file.exists() || file.length() <= 2) {
            System.out.println("No employees\n");
            return;
        }
        try {
            Object empData = parser.parse(new FileReader(file));
            JSONArray array = (JSONArray) empData;

            System.out.println("\nEmployee Details\n");
            for (Object obj : array) {
                JSONObject jsonObject = (JSONObject) obj;
                System.out.println(
                        "Emp ID: " + jsonObject.get("id")
                                + " | Name: " + jsonObject.get("name")
                                + " | Department: " + jsonObject.get("department")
                                + " | Address: " + jsonObject.get("address")
                                + " | Email: " + jsonObject.get("email")
                                + " | Roles: " + jsonObject.get("role")
                );
            }
            System.out.println();
        } catch (IOException e) {
            System.out.println("Error regarding File");
        } catch (ParseException e) {
            System.out.println("Parser error");
        }
    }

    public void view_by_id() {

        CheckEmployees check = new CheckEmployees();

        try {
            String id;
            if ("USER".equals(CheckLogin.role)) {
                id = CheckLogin.id;
            } else {
                System.out.print("Enter emp id: ");
                id = Input.SC.nextLine().trim();
            }

            boolean present = check.checkEmployee(id);

            Object empData = parser.parse(new FileReader(file));
            JSONArray array = (JSONArray) empData;

            if (present) {
                for (Object obj : array) {
                    JSONObject jsonObject = (JSONObject) obj;
                    String currId = (String) jsonObject.get("id");

                    if (id.equals(currId)) {
                        System.out.println("Employee Detail\n"); 
                        System.out.println(
                                "Emp ID: " + jsonObject.get("id")
                                        + " | Name: " + jsonObject.get("name")
                                        + " | Department: " + jsonObject.get("department")
                                        + " | Address: " + jsonObject.get("address")
                                        + " | Email: " + jsonObject.get("email")
                                        + " | Roles: " + jsonObject.get("role")
                        );
                        System.out.println();
                        break;
                    }
                }
            } else {
                throw new EmployeeNotFoundException("Employee doesn't exist");
            }

        } catch (IOException e) {
            System.out.println("Error");
        } catch (ParseException e) {
            System.out.println("Parser Error");
        } catch (EmployeeNotFoundException e) {
            e.printStackTrace();
        }
    }
}