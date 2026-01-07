package empUtil;

import model.Employee;
import store.DataStore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FileUtil { 
    private static final String FILE_NAME = "employees.json"; 
    private static final ObjectMapper mapper = new ObjectMapper(); 
    private static File file; 
    private FileUtil() {} 
    public static DataStore loadOrCreateStore() {
        DataStore store = new DataStore();
        file = new File(FILE_NAME); 
        if (file.exists() && file.length() > 0) {
            try { 
                List<Employee> employees =
                        mapper.readValue(file, new TypeReference<List<Employee>>() {});
                for (Employee emp : employees) {
                    store.getEmployees().put(emp.getId(), emp); 
                    int idNumber = extractNumber(emp.getId()); 
                    if (idNumber > store.getLastId()) {
                        store.setLastId(idNumber);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error while loading employee file");
            }
        } 
        if (!store.getEmployees().containsKey("tek0")) {
            Employee admin = new Employee();
            admin.setId("tek0");
            admin.setName("Admin");
            admin.setDepartment("ADMIN");
            admin.setAddress("System");
            admin.setEmail("admin@system.com");
            admin.setRole(List.of("ADMIN"));
            admin.setPassword(PasswordUtil.sha1("pass123"));
            store.getEmployees().put("tek0", admin);
        } 
        saveStore(store);
        return store;
    } 
    public static void saveStore(DataStore store) {
        List<Employee> list = new ArrayList<>(store.getEmployees().values()); 
        list.sort(Comparator.comparingInt(
                emp -> extractNumber(emp.getId())
        ));
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, list);
        } catch (Exception e) {
            System.out.println("Error while saving employee file");
        }
    } 
    private static int extractNumber(String id) {
        if (id == null) {
            return 0;
        }
        id = id.toLowerCase().trim();
        if (!id.startsWith("tek")) {
            return 0;
        }
        try {
            return Integer.parseInt(id.substring(3));
        } catch (Exception e) {
            return 0;
        }
    }
}
