package controller;

import methods.CheckLogin;

public class LoginController {
    public static boolean loginCheck() {
        while (true) {
            System.out.println("Login details\n");
            System.out.print("Enter Employee ID: ");
            String id = Input.SC.nextLine().trim();
            System.out.print("Enter Password: ");
            String password = Input.SC.nextLine().trim();
            boolean validUser = CheckLogin.validateLogin(id, password);
            if (validUser) {
                return true;
            } else {
                System.out.println("Invalid login, try again.\n");
            }
        }
    }
}