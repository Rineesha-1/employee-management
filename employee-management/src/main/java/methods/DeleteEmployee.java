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

public class DeleteEmployee {
    public void delete() {

        CheckEmployees checkEmployees = new CheckEmployees();
        ViewEmployee getEmployee = new ViewEmployee();

        try {
            System.out.print("Enter empId to delete: ");
            String delId = Input.SC.nextLine().trim();

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(getEmployee.file));
            JSONArray array = (JSONArray) obj;

            boolean present = checkEmployees.checkEmployee(delId);

            if (!present) {
                throw new EmployeeNotFoundException("Employee doesn't exist");
            }

            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObject = (JSONObject) array.get(i);
                String id = (String) jsonObject.get("id");
                if (delId.equals(id)) {
                    array.remove(i);
                    break;
                }
            }

            FileWriter fw = new FileWriter(getEmployee.file);
            fw.write(array.toJSONString());
            fw.flush();
            fw.close();

            System.out.println("Employee Deleted successfully\n");
            getEmployee.view_all();

        } catch (IOException e) {
            System.out.println("Error regarding File");
        } catch (ParseException e) {
            System.out.println("Parser error");
        } catch (EmployeeNotFoundException e) {
            e.printStackTrace();
        }
    }
}