package controller;

import services.AuthService;
import dao.EmployeeDAO;
import store.DataStore;
import java.util.Scanner;

public class LoginController {
    private LoginController() {}
    public static AuthService login(DataStore store,EmployeeDAO dao,Scanner sc) {
    	AuthService auth=new AuthService(store, dao,sc);
    	auth.login();
    	return auth;
    }
}