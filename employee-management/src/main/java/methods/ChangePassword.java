package methods;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import controller.Input;
public class ChangePassword {
    @SuppressWarnings("unchecked")
    public boolean changePassword() {
        JSONParser parser = new JSONParser();
        ViewEmployee getEmployee = new ViewEmployee();
        String id = CheckLogin.id;
        if (!getEmployee.file.exists() || getEmployee.file.length() <= 2) {
            System.out.println("No employees\n");
            return false;
        }
        try {
            Object empData = parser.parse(new FileReader(getEmployee.file));
            JSONArray array = (JSONArray) empData;
            System.out.print("Enter new password: ");
            String password = Input.SC.nextLine();
            System.out.print("Re-Enter new password: ");
            String samePassword = Input.SC.nextLine();
            if (!password.equals(samePassword)) {
                System.out.println("Passwords do not match\n");
                return false;
            } 
            if (password.equals(ResetPassword.defaultPass)) {
                System.out.println("New password cannot be the default password\n");
                return false;
            }
            String hashPassword = Password.hash(password);
            for (Object obj : array) {
                JSONObject jsonObject = (JSONObject) obj;
                String currId = (String) jsonObject.get("id");
                if (currId.equals(id)) {
                    jsonObject.replace("password", hashPassword);
                    FileWriter fw = new FileWriter(getEmployee.file);
                    fw.write(array.toJSONString());
                    fw.flush();
                    fw.close();
                    System.out.println("Successfully changed password\n");
                    return true; 
                }
            }
        } catch (ParseException e) {
            System.out.println("Parser error");
        } catch (IOException e) {
            System.out.println("Error writing to the file");
        }
        return false;
    }
}