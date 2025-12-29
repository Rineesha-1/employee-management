package methods;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import controller.Input;
import exceptions.EmployeeNotFoundException;

public class UpdateEmployee {
    CheckEmployees checkEmployees = new CheckEmployees();
    ViewEmployee viewEmployee = new ViewEmployee();
    @SuppressWarnings("unchecked")
    public void update() {
        try {
            String id;
            if (CheckLogin.role.equals("USER")) {
                id = CheckLogin.id;
            } else {
                System.out.print("Enter emp id: ");
                id = Input.SC.nextLine().trim();
            }
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(viewEmployee.file));
            JSONArray array = (JSONArray) obj;
            boolean present = checkEmployees.checkEmployee(id);
            if (!present) {
                throw new EmployeeNotFoundException("Employee doesn't exist");
            }
            JSONObject target = null;
            for (Object o : array) {
                JSONObject jsonObject = (JSONObject) o;
                if (jsonObject.get("id").equals(id)) {
                    target = jsonObject;
                    break;
                }
            }
            if (target == null) {
                throw new EmployeeNotFoundException("Employee doesn't exist");
            }
            boolean done = false;
            while (!done) {
                System.out.println("\nUpdate Options:"); 
                if (!CheckLogin.role.equals("USER")) {
                    System.out.println(UpdateChoices.NAME);
                    System.out.println(UpdateChoices.DEPARTMENT);
                }
                System.out.println(UpdateChoices.ADDRESS);
                System.out.println(UpdateChoices.EMAIL);
                System.out.println(UpdateChoices.ALL);
                System.out.println(UpdateChoices.EXIT);
                System.out.print("\nType your Choice: ");
                String input = Input.SC.nextLine().trim();
                UpdateChoices choice;
                try {
                    choice = UpdateChoices.valueOf(input.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid choice");
                    continue;
                }
                switch (choice) {
                    case NAME:
                        if (CheckLogin.role.equals("USER")) {
                            System.out.println("USER cannot update NAME");
                            break;
                        }
                        System.out.print("Enter new name: ");
                        target.put("name", Input.SC.nextLine());
                        System.out.println("Name updated");
                        break;
                    case DEPARTMENT:
                        if (CheckLogin.role.equals("USER")) {
                            System.out.println("USER cannot update DEPARTMENT");
                            break;
                        }
                        System.out.print("Enter new department: ");
                        target.put("department", Input.SC.nextLine());
                        System.out.println("Department updated");
                        break;
                    case ADDRESS:
                        System.out.print("Enter new address: ");
                        target.put("address", Input.SC.nextLine());
                        System.out.println("Address updated");
                        break;
                    case EMAIL:
                        System.out.print("Enter new email: ");
                        target.put("email", Input.SC.nextLine());
                        System.out.println("Email updated");
                        break;
                    case ALL:
                        if (!CheckLogin.role.equals("USER")) {
                            System.out.print("Enter new name: ");
                            target.put("name", Input.SC.nextLine());

                            System.out.print("Enter new department: ");
                            target.put("department", Input.SC.nextLine());
                        }
                        System.out.print("Enter new address: ");
                        target.put("address", Input.SC.nextLine());

                        System.out.print("Enter new email: ");
                        target.put("email", Input.SC.nextLine());
                        System.out.println("Updated");
                        break;
                    case EXIT:
                        done = true;
                        break;
                }
                FileWriter fw = new FileWriter(viewEmployee.file);
                fw.write(array.toJSONString());
                fw.flush();
                fw.close();
            }
            System.out.println();
            if (!CheckLogin.role.equals("USER")) viewEmployee.view_all();
            else viewEmployee.view_by_id();
        } catch (IOException e) {
            System.out.println("File error");
        } catch (ParseException e) {
            System.out.println("JSON parse error");
        } catch (EmployeeNotFoundException e) {
            e.printStackTrace();
        }
    }
}