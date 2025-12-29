package methods;

import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CheckLogin {
    public static String role;
    public static String id;
    public static boolean firstLogin;

    public static boolean validateLogin(String id, String password) {

        ViewEmployee getEmployee = new ViewEmployee();
        JSONParser parser = new JSONParser();

        if (!getEmployee.file.exists() || getEmployee.file.length() <= 2) {
            System.out.println("No login records\n");
            return false;
        }

        try {
            Object loginData = parser.parse(new FileReader(getEmployee.file));
            JSONArray array = (JSONArray) loginData;

            String hashPassword = Password.hash(password);
            String defaultHash = Password.hash(ResetPassword.defaultPass);

            for (Object obj : array) {
                JSONObject jsonObject = (JSONObject) obj;

                String jsonId = (String) jsonObject.get("id");
                String jsonPassword = (String) jsonObject.get("password");

                if (jsonId.equals(id)) {
                    if (jsonPassword.equals(hashPassword)) {

                        System.out.println("Login Successful\n");
                        CheckLogin.id = jsonId;

                        JSONArray roleArray = (JSONArray) jsonObject.get("role"); 
                        if (roleArray.contains("ADMIN")) {
                            CheckLogin.role = "ADMIN";
                        } else if (roleArray.contains("MANAGER")) {
                            CheckLogin.role = "MANAGER";
                        } else {
                            CheckLogin.role = "USER";
                        }
                        CheckLogin.firstLogin = jsonPassword.equals(defaultHash);

                        return true;
                    }
                    return false;
                }
            }
            return false;

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}