package store;

import model.Employee;
import java.util.HashMap;
import java.util.Map;

public class DataStore {
    private Map<String, Employee> employees = new HashMap<>();
    private int lastId;
    public Map<String, Employee> getEmployees() {
        return employees;
    }
    public int getLastId() {
        return lastId;
    }
    public void setLastId(int lastId) {
        this.lastId = lastId;
    }
}