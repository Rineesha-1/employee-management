package methods; 
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AutoId {
	public String genId() {
		ViewEmployee readEmployees = new ViewEmployee();
		try {
			JSONParser parser = new JSONParser();
			Object empData = parser.parse(new FileReader(readEmployees.file));
			JSONArray array = (JSONArray) empData;
			
			int len = array.size();
			if(len >= 1) {
				JSONObject jsonObject = (JSONObject) array.get(len-1); 
				String id = (String) jsonObject.get("id");
				int suffix = Integer.parseInt(id.substring(3));
				suffix = suffix+1;
				return "tek"+suffix;
			}
		}
		catch (ParseException e) {
			System.out.println("Parser error");
		}
		catch(IOException e) {
			System.out.println("Error writing to the file");
		}
		return "tek1";
	}
}