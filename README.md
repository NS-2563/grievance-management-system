# Grievance Management System

A simple Java-based application designed to streamline the process of submitting, tracking, and managing user grievances.
This project allows users to raise issues, view their grievance history, and search or monitor the resolution progress efficiently.

---

## 🚀 Features

* User login and registration
* Raise new grievances
* View personal grievances and their status
* Search and view all grievances
* Admin panel for grievance management
* MySQL database integration

---

## 🧩 Technologies Used

* **Frontend:** Java 
* **Backend:** Java
* **Database:** MySQL
* **Tools/IDE:** NetBeans / Eclipse / IntelliJ
* **Version Control:** Git & GitHub

---

## 🗄️ Database Setup

1. Open **Command Prompt**.
2. Create a database in MySQL:

   ```bash
   mysql -u root -p -e "CREATE DATABASE grievance_system;"
   ```
3. Import the SQL dump file:

   ```bash
   mysql -u root -p grievance_system < grievance.sql
   ```
4. Update your `DBUtil.java` file (or configuration file) with your MySQL username and password.

---

## ⚙️ How to Run

1. Open the project in your IDE (e.g. NetBeans, Eclipse, or IntelliJ).
2. Ensure your MySQL server is running.
3. Update database connection details in your Java code if required.
4. Run the main file — the application will start and connect to the database.

---

## 📂 Project Structure

```
GrievanceManagementSystem/
 ├── src/                     # Source code files
 ├── grievance.sql            # MySQL database dump
 ├── README.md                # Project documentation
 └── .gitignore               # Git ignore rules
```

---

## 🧠 Future Enhancements

* Add email/SMS notification system
* Role-based dashboard for better management
* Graphical reports on grievance status

---

## 👨‍💻 Author

**[Your Name]**
Developed as part of an academic project using Java and MySQL.

---

## 🪪 License

This project is open-source and available under the [MIT License](LICENSE).

