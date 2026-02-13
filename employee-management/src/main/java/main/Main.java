package main; 

import controller.StorageController; 
import exceptions.EmployeeDataAccessException; 

public class Main { 
	public static void main(String[] args) throws EmployeeDataAccessException { 
		StorageController.startApplication(); 
	} 
}