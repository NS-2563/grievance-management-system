package com.grievance.cli;

import com.grievance.dao.GrievanceDAO;
import com.grievance.dao.UserDAO;
import com.grievance.model.Grievance;
import com.grievance.model.User;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for the Grievance Handling System (Command Line Interface).
 */
public class GrievanceSystemCLI {

    private static User currentUser = null;
    private static Scanner scanner = new Scanner(System.in);
    private static UserDAO userDAO = new UserDAO();
    private static GrievanceDAO grievanceDAO = new GrievanceDAO();

    public static void main(String[] args) {
        showWelcomeScreen();
    }

    // --- Main Flow & Menus ---
    private static void showWelcomeScreen() {
        int choice = -1;
        while (currentUser == null) {
            System.out.println("\n===== Grievance Handling System =====");
            System.out.println("1. Login");
            System.out.println("2. Register (as new User)");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1: loginUser(); break;
                    case 2: registerUser(); break;
                    case 0: System.out.println("Exiting application. Goodbye!"); return;
                    default: System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear buffer
            }
        }
        showMainMenu();
    }

    private static void showMainMenu() {
        int choice = -1;

        while (currentUser != null) {
            System.out.println("\nWelcome, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
            System.out.println("\n===== Main Menu =====");

            switch (currentUser.getRole()) {
                case "USER":
                    System.out.println("1. Raise New Grievance");
                    System.out.println("2. View My Grievances");
                    System.out.println("3. View All Grievances");
                    System.out.println("4. Search Grievances");
                    break; 
                case "GRIEVANCE_MANAGER":
                    System.out.println("1. View All Grievances");
                    System.out.println("2. Update Grievance Status");
                    System.out.println("3. Search Grievances");
                    System.out.println("4. Reports (Open/Resolved)");
                    break; 
                case "ADMINISTRATOR":
                    System.out.println("1. View All Grievances (All Roles)");
                    System.out.println("2. Update Grievance Status (All Roles)");
                    System.out.println("3. Administration (User/Role Management)");
                    System.out.println("4. Search Grievances");
                    System.out.println("5. Reports (Open/Resolved)");
                    break; 
                default:
                    System.out.println("Unknown role.");
                    break;
            }

            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); 

                if (choice == 0) {
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    showWelcomeScreen();
                    return;
                }

                handleUserChoice(choice);
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }
    
    


    // --- Authentication Logic ---
    private static void loginUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userDAO.login(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login successful!");
        } else {
            System.out.println("Login failed: Invalid username or password.");
        }
    }

    private static void registerUser() {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();
        System.out.print("Enter new password: ");
        String password = scanner.nextLine();

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        if (userDAO.register(username, password)) {
            System.out.println("Registration successful! You can now log in.");
        }
    }

    // --- Grievance Logic ---
    private static void handleUserChoice(int choice) {
        if ("USER".equals(currentUser.getRole())) {
            switch (choice) {
                case 1:
                    raiseGrievance();
                    break;
                case 2:
                    viewMyGrievances(); 
                    break;
                case 3:
                    viewAllGrievances();
                    break;
                case 4:
                    searchGrievancesCLI(); 
                    break;
                default:
                    System.out.println("Invalid choice for your role.");
            }
        } else if ("GRIEVANCE_MANAGER".equals(currentUser.getRole())) {
            switch (choice) {
                case 1:
                    viewAllGrievances();
                    break;
                case 2:
                    updateGrievanceStatus();
                    break;
                case 3:
                    searchGrievancesCLI(); 
                    break;
                case 4: 
                	showReports(); 
                	break;
                default:
                    System.out.println("Invalid choice for your role.");
            }
        } else if ("ADMINISTRATOR".equals(currentUser.getRole())) {
            switch (choice) {
                case 1:
                    viewAllGrievances();
                    break;
                case 2:
                    updateGrievanceStatus();
                    break;
                case 3:
                    showAdminMenu();
                    break;
                case 4:
                    searchGrievancesCLI(); 
                    break;
                case 5: 
                	showReports(); 
                	break;
                default:
                    System.out.println("Invalid choice for your role.");
            }
        }
    }


    private static void searchGrievancesCLI() {
        System.out.print("Enter keyword to search (title/description): ");
        String keyword = scanner.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println("Keyword cannot be empty.");
            return;
        }

        List<Grievance> results = grievanceDAO.searchGrievances(keyword);

        if (results.isEmpty()) {
            System.out.println("No grievances found matching the keyword.");
            return;
        }

        System.out.println("\n--- Search Results ---");
        for (Grievance g : results) {
            System.out.println(g);
        }
    }

    

    private static void raiseGrievance() {
        System.out.println("\n--- Raise New Grievance ---");
        System.out.print("Enter Title (Max 255 chars): ");
        String title = scanner.nextLine();
        System.out.println("Enter Description (multi-line supported): ");
        String description = scanner.nextLine();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            System.out.println("Title and description cannot be empty.");
            return;
        }

        Grievance newGrievance = new Grievance(currentUser.getId(), title, description);
        if (grievanceDAO.createGrievance(newGrievance)) {
            System.out.println("\n✅ Grievance submitted successfully! Status: OPEN");
        } else {
            System.out.println("\n❌ Failed to submit grievance. Check database connection.");
        }
    }

    private static void viewAllGrievances() {
        System.out.println("\n--- All System Grievances ---");
        List<Grievance> grievances = grievanceDAO.getAllGrievances();
        if (grievances.isEmpty()) {
            System.out.println("No grievances found in the system.");
            return;
        }
        System.out.println("Total Grievances: " + grievances.size());
        System.out.println("----------------------------------------------------------------------------------------------------");
        for (Grievance g : grievances) {
            System.out.println(g);
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
    }
    
    private static void viewMyGrievances() {
        System.out.println("\n--- My Grievances ---");
        List<Grievance> grievances = grievanceDAO.getGrievancesByUserId(currentUser.getId());

        if (grievances.isEmpty()) {
            System.out.println("You have not submitted any grievances yet.");
            return;
        }

        System.out.println("Total Grievances: " + grievances.size());
        System.out.println("------------------------------------------------------------------------------------------------");
        for (Grievance g : grievances) {
            System.out.println(g);
        }
        System.out.println("------------------------------------------------------------------------------------------------");
    }


    private static void updateGrievanceStatus() {
        System.out.println("\n--- Update Grievance Status ---");
        viewAllGrievances();

        System.out.print("Enter the ID of the Grievance to update: ");
        int id;
        try { id = scanner.nextInt(); scanner.nextLine(); } 
        catch (InputMismatchException e) { System.out.println("Invalid ID."); scanner.nextLine(); return; }

        System.out.println("Choose new status:");
        System.out.print("1. IN_PROGRESS\n2. RESOLVED\nEnter choice: ");  // Use print instead of println
        int statusChoice = -1;
        try {
            statusChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.out.println("Invalid choice. Must be a number.");
            scanner.nextLine();
            return;
        }
        
        


        String newStatus = null;
        switch (statusChoice) {
            case 1: newStatus = "IN_PROGRESS"; break;
            case 2: newStatus = "RESOLVED"; break;
            default: System.out.println("Invalid status choice."); return;
        }

        if (grievanceDAO.updateGrievanceStatus(id, newStatus)) {
            System.out.println("✅ Grievance ID " + id + " status updated to " + newStatus);
        } else {
            System.out.println("❌ Failed to update grievance ID " + id);
        }
    }
    
    private static void showReports() {
        System.out.println("\n--- Grievance Reports ---");
        int open = grievanceDAO.countByStatus("OPEN");
        int inProgress = grievanceDAO.countByStatus("IN_PROGRESS");
        int resolved = grievanceDAO.countByStatus("RESOLVED");

        System.out.println("OPEN Complaints      : " + open);
        System.out.println("IN_PROGRESS Complaints: " + inProgress);
        System.out.println("RESOLVED Complaints  : " + resolved);
    }

    
    

    // --- ADMIN MENU METHODS ---
    private static void showAdminMenu() {
        int choice = -1;
        while (true) {
            System.out.println("\n===== ADMINISTRATION MENU =====");
            System.out.println("1. View All Users");
            System.out.println("2. Add New User");
            System.out.println("3. Update User Role");
            System.out.println("4. Delete User");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");

            try {
                choice = scanner.nextInt(); scanner.nextLine();
                switch (choice) {
                    case 1: viewAllUsers(); break;
                    case 2: addNewUser(); break;
                    case 3: updateUserRole(); break;
                    case 4: deleteUser(); break;
                    case 0: return;
                    default: System.out.println("Invalid choice.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

 // --- Admin Helper Methods ---
    private static void viewAllUsers() {
        List<User> users = userDAO.getAllUsers();
        System.out.println("\n--- ALL USERS ---");
        if (users.isEmpty()) { System.out.println("No users found."); return; }
        System.out.println("ID | Username       | Role");
        System.out.println("-----------------------------------");
        for (User u : users) {
            System.out.printf("%-3d| %-14s| %-15s%n", u.getId(), u.getUsername(), u.getRole());
        }
    }

    private static void addNewUser() {
        System.out.print("Enter username: "); String username = scanner.nextLine();
        System.out.print("Enter password: "); String password = scanner.nextLine();
        System.out.print("Enter role (USER / GRIEVANCE_MANAGER / ADMINISTRATOR): ");
        String role = scanner.nextLine().toUpperCase();

        if (userDAO.registerWithRole(username, password, role)) {
            System.out.println("User created successfully!");
        } else { System.out.println("Failed to create user."); }
    }

    private static void updateUserRole() {
        viewAllUsers();
        System.out.print("Enter User ID to update role: "); int userId = scanner.nextInt(); scanner.nextLine();
        System.out.print("Enter new role (USER / GRIEVANCE_MANAGER / ADMINISTRATOR): ");
        String newRole = scanner.nextLine().toUpperCase();

        if (userDAO.updateUserRole(userId, newRole)) {
            System.out.println("User role updated successfully.");
        } else { System.out.println("Failed to update user role."); }
    }

    private static void deleteUser() {
        viewAllUsers();
        System.out.print("Enter User ID to delete: "); int userId = scanner.nextInt(); scanner.nextLine();
        System.out.print("Are you sure? (Y/N): "); String confirm = scanner.nextLine();
        if (!confirm.equalsIgnoreCase("Y")) return;

        if (userDAO.deleteUser(userId)) {
            System.out.println("User deleted successfully.");
        } else { System.out.println("Failed to delete user."); }
    }
}
