package controller;

import dao.EmployeeDAO;
import dao.EmployeeJsonDAOImpl;
import dao.EmployeeJdbcDAOImpl;
import enums.StorageType;
import java.util.Scanner;

public class StorageController {
	private StorageController() {
	}

	public static void startApplication() {
		Scanner sc = new Scanner(System.in);
		EmployeeDAO dao = chooseStorage(sc);
		MenuController.start(dao, sc);
	}

	private static EmployeeDAO chooseStorage(Scanner sc) {
		System.out.println("EMPLOYEE MANAGEMENT SYSTEM");
		while (true) {
			System.out.println("Select storage type (FILE / DATABASE):");
			System.out.print("Enter choice: ");
			String input = sc.nextLine().trim().toUpperCase();
			try {
				StorageType type = StorageType.valueOf(input);
				switch (type) {
				case DATABASE:
					return new EmployeeJdbcDAOImpl();
				case FILE:
				default:
					return new EmployeeJsonDAOImpl();
				}
			} catch (IllegalArgumentException e) {
				System.out.println("Invalid choice.Try again");
			}
		}
	}
}