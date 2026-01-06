package controller;

import services.AuthService;
import store.DataStore;

public class LoginController {
    private LoginController() {}
    public static AuthService login(DataStore store) {
    	AuthService auth=new AuthService(store);
    	auth.login();
    	return auth;
    }
}