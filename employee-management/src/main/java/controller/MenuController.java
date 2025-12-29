package controller;

import methods.AddEmployee;
import methods.ChangePassword;
import methods.ChangeRole;
import methods.CheckLogin;
import methods.DeleteEmployee;
import methods.ViewEmployee;
import methods.ResetPassword;
import methods.UpdateEmployee;

public class MenuController {
    public static void displayMenu() {
        if (LoginController.loginCheck()) {
            ViewEmployee readEmployees = new ViewEmployee();
            DeleteEmployee deleteEmployees = new DeleteEmployee();
            UpdateEmployee updateEmployees = new UpdateEmployee();
            ChangePassword changePass = new ChangePassword();
            ResetPassword resetPass = new ResetPassword();
            AddEmployee addEmployee = new AddEmployee();
            ChangeRole changeRole = new ChangeRole();  
            if (CheckLogin.firstLogin) {
                System.out.println("Change default password.\n");
                boolean changed = false;
                while (!changed) {
                    changed = changePass.changePassword();
                }
                CheckLogin.firstLogin = false;
            }
            boolean exit = false;
            System.out.println();
            System.out.println("EMPLOYEE MANAGEMENT SYSTEM");
            System.out.println();
            while (!exit) {
                String role = CheckLogin.role;
                if (role.equals("ADMIN")) {
                    System.out.println("ADMIN Operations\n");
                    for (AdminChoices c : AdminChoices.values()) {
                        System.out.println(c);
                    }
                    try {
                        System.out.println();
                        System.out.print("Type your Choice: ");
                        String input = Input.SC.nextLine().trim();
                        AdminChoices choice = AdminChoices.valueOf(input.toUpperCase());
                        switch (choice) {
                            case ADD: addEmployee.insert(); break;
                            case VIEW: readEmployees.view_all(); break;
                            case DELETE: deleteEmployees.delete(); break;
                            case UPDATE: updateEmployees.update(); break;
                            case VIEW_BY_ID: readEmployees.view_by_id(); break;
                            case RESET_PASSWORD: resetPass.resetPassword(); break;
                            case GRANT_ROLE: changeRole.grantRole(); break;
                            case REVOKE_ROLE: changeRole.revokeRole(); break;
                            case EXIT: exit = true; break;
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid choice\n");
                    }
                } else if (role.equals("MANAGER")) {
                    System.out.println("MANAGER Operations\n");
                    for (ManagerChoices c : ManagerChoices.values()) {
                        System.out.println(c);
                    }
                    try {
                        System.out.println();
                        System.out.print("Type your Choice: ");
                        String input = Input.SC.nextLine().trim();
                        ManagerChoices choice = ManagerChoices.valueOf(input.toUpperCase());
                        switch (choice) {
                            case VIEW: readEmployees.view_all(); break;
                            case UPDATE: updateEmployees.update(); break;
                            case VIEW_BY_ID: readEmployees.view_by_id(); break;
                            case EXIT: exit = true; break;
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid choice\n");
                    }
                } else {
                    System.out.println("USER Operations\n");
                    for (UserChoices c : UserChoices.values()) {
                        System.out.println(c);
                    }
                    try {
                        System.out.println();
                        System.out.print("Type your Choice: ");
                        String input = Input.SC.nextLine().trim();
                        UserChoices choice = UserChoices.valueOf(input.toUpperCase());
                        switch (choice) {
                            case VIEW: readEmployees.view_by_id(); break;
                            case CHANGE_PASSWORD: changePass.changePassword(); break;
                            case UPDATE: updateEmployees.update(); break;
                            case EXIT: exit = true; break;
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid choice\n");
                    }
                }
            }
        }
    }
}