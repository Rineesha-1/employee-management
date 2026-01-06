package empUtil;

import model.Employee;
import store.DataStore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FileUtil {

    private static final String FILE_NAME = "employees.json";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static File FILE;
    private FileUtil() {}
    public static DataStore loadOrCreateStore() {
        DataStore store = new DataStore();
        FILE = resolveFile(FILE_NAME);
        if (FILE.exists() && FILE.length() > 0) {
            try {
                List<Employee> list = MAPPER.readValue(FILE, new TypeReference<List<Employee>>() {});
                for (Employee e : list) {
                    store.getEmployees().put(e.getId(), e);
                    int n = extractNum(e.getId());
                    if (n > store.getLastId()) store.setLastId(n);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error loading file");
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
        store.setLastId(Math.max(store.getLastId(), 0));
        saveStore(store);
        return store;
    }
    public static void saveStore(DataStore store) {
        if (FILE == null) FILE = resolveFile(FILE_NAME);
        List<Employee> list = new ArrayList<>(store.getEmployees().values());
        list.sort(Comparator.comparingInt(e -> extractNum(e.getId())));
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(FILE, list);
        } catch (Exception e) {
            throw new RuntimeException("Error saving employees.json");
        }
    }
    private static File resolveFile(String fileName) {
        File local = new File(fileName);
        if (local.exists() || canCreate(local)) return local;
        try {
            return getFileFromResource(fileName);
        } catch (Exception e) {
            return local;
        }
    }
    private static boolean canCreate(File f) {
        try {
            if (f.exists()) return true;
            File parent = f.getAbsoluteFile().getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            return f.createNewFile();
        } catch (Exception e) {
            return false;
        }
    }
    private static File getFileFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) throw new IllegalArgumentException("file not found! " + fileName);
        return new File(resource.toURI());
    }
    private static int extractNum(String id) {
        if (id == null) return 0;
        String s = id.trim().toLowerCase();
        if (!s.startsWith("tek")) return 0;
        try {
            return Integer.parseInt(s.substring(3));
        } catch (Exception e) {
            return 0;
        }
    }
}