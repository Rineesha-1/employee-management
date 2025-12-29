package methods;

import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CheckEmployees {
	public boolean checkEmployee(String checkId) {
		ViewEmployee viewEmployees = new ViewEmployee();
		JSONParser parser = new JSONParser();
		try {
			Object empData = parser.parse(new FileReader(viewEmployees.file));
			JSONArray array = (JSONArray) empData;
			for (Object obj : array) { 
				JSONObject jsonObject = (JSONObject) obj;
				String id = (String) jsonObject.get("id");
				if (id.equals(checkId)) {
					return true;
				}
			}
			return false;
		} catch (IOException e) {
			System.out.println("Error");
		} catch (ParseException e) {
			System.out.println("Parser Error");
		}
		return false;
	}
}