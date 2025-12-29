package methods;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import controller.Input;

public class ResetPassword {
    public static String defaultPass = "pass123";

    @SuppressWarnings("unchecked")
    public void resetPassword() {

        JSONParser parser = new JSONParser();
        ViewEmployee getEmployee = new ViewEmployee();

        if (!getEmployee.file.exists() || getEmployee.file.length() <= 2) {
            System.out.println("No employees\n");
            return;
        }

        try {
            Object empData = parser.parse(new FileReader(getEmployee.file));
            JSONArray array = (JSONArray) empData;

            System.out.print("Enter employee id to reset password: ");
            String id = Input.SC.nextLine().trim();

            String hashPassword = Password.hash(defaultPass);

            for (Object obj : array) {
                JSONObject jsonObject = (JSONObject) obj;
                String currId = (String) jsonObject.get("id");

                if (currId.equals(id)) {
                    jsonObject.replace("password", hashPassword);

                    FileWriter fw = new FileWriter(getEmployee.file);
                    fw.write(array.toJSONString());
                    fw.flush();
                    fw.close();

                    System.out.println("Changed password to default\n");
                    return;
                }
            }

            System.out.println("Employee not found\n");

        } catch (ParseException e) {
            System.out.println("Parser error");
        } catch (IOException e) {
            System.out.println("Error writing to the file");
        }
    }
}