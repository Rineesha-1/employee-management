package controller;

import dao.EmployeeDAO;
import dao.EmployeeJsonDAOImpl;
import dao.EmployeeJdbcDAOImpl;
import enums.StorageType;
import exceptions.EmployeeDataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class StorageController {
	private static final Logger logger = LoggerFactory.getLogger(StorageController.class);
	private StorageController() {
	}
	//starts the application
	public static void startApplication() throws EmployeeDataAccessException {
		Scanner sc = new Scanner(System.in);
		logger.info("Application started");
		EmployeeDAO dao = chooseStorage(sc); 
		MenuController.start(dao, sc);
	}
	//selects storage type
	private static EmployeeDAO chooseStorage(Scanner sc) throws EmployeeDataAccessException {
	    System.out.println("EMPLOYEE MANAGEMENT SYSTEM");
	    int attempts = 0;
	    while (attempts < 3) {
	        System.out.println("Select storage type (FILE / DATABASE):");
	        System.out.print("Enter choice: ");
	        String input = sc.nextLine();
	        if (input == null || input.trim().isEmpty()) {
	            System.out.println("Input cannot be empty. Try again.");
	            attempts++;
	            continue;
	        }
	        input = input.trim().toUpperCase();
	        try {
	            StorageType type = StorageType.valueOf(input);
	            logger.info("Storage selected: {}", type);
	            switch (type) {
	                case DATABASE:
	                    return new EmployeeJdbcDAOImpl();
	                case FILE:
	                    return new EmployeeJsonDAOImpl();
	            }
	        } catch (IllegalArgumentException e) {
	            System.out.println("Invalid choice. Try again.");
	            attempts++;
	        }
	    }
	    System.out.println("Max attempts reached. Exiting application...");
	    System.exit(0);
	    return null;  
	}
}