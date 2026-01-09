package main;

import controller.MenuController;
import dao.EmployeeJsonDAOImpl;
import store.DataStore;
import dao.EmployeeDAO;

public class Main {
    public static void main(String[] args) {
        DataStore store = new DataStore();
        EmployeeDAO dao=new EmployeeJsonDAOImpl(store);
        MenuController.start(store,dao); 
    }
}
