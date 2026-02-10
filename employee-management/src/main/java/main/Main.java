package main;

import controller.StorageController;
import exceptions.EmployeeDataAccessException;

public class Main {
    public static void main(String[] args) {
        try {
            StorageController.startApplication();
        } catch (EmployeeDataAccessException e) {
            System.out.println("Application failed to start due to a system error.");
            e.printStackTrace();
        }
    }
}
