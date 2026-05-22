import java.sql.*;
import java.util.Scanner;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
             UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
}

        Scanner sc = new Scanner(System.in);

        System.out.println("===================================");
        System.out.println("     STUDENT PLACEMENT SYSTEM      ");
        System.out.println("===================================");
        System.out.println("1. Student Login");
        System.out.println("2. Admin Login");
        System.out.print("Enter choice: ");

        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            studentLogin();
        } else if (choice == 2) {
            adminLogin();
        } else {
            System.out.println("Invalid Choice");
        }
    }

    // ================= STUDENT LOGIN =================
    public static void studentLogin() {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Student ID: ");
        int studentId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        try {
            Connection con = DBConnection.connect();

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM students WHERE student_id = ? AND password = ?");
            ps.setInt(1, studentId);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String name = rs.getString("name");
                double gpa = rs.getDouble("gpa");
                String skills = rs.getString("skills");
                String department = rs.getString("department");
                String email = rs.getString("email");
                String resumePath = rs.getString("resume_path");
                String status = rs.getString("status");

                System.out.println("\n===================================");
                System.out.println("           STUDENT PROFILE         ");
                System.out.println("===================================");
                System.out.println("Name        : " + name);
                System.out.println("Department  : " + department);
                System.out.println("Email       : " + email);
                System.out.println("GPA         : " + gpa);
                System.out.println("Skills      : " + skills);
                System.out.println("Resume Path : " + resumePath);
                System.out.println("Status      : " + status);
                System.out.println("===================================");

                if (status.equalsIgnoreCase("Placed")) {
                    System.out.println("You are already placed. Applications closed.");
                } else {
                    showEligibleJobs(con, studentId, gpa, skills);
                }

            } else {
                System.out.println("Invalid Student ID or Password ❌");
            }

            con.close();

        } catch (SQLException e) {
            System.out.println("Database error occurred.");
        }
    }

    // ================= SHOW ELIGIBLE JOBS =================
    public static void showEligibleJobs(Connection con, int studentId, double gpa, String skills) {

        Scanner sc = new Scanner(System.in);

        try {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM job_postings WHERE min_cgpa <= ? AND required_skills LIKE CONCAT('%', ?, '%')");
            ps.setDouble(1, gpa);
            ps.setString(2, skills);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nEligible Jobs:");
            System.out.println("----------------------------------------------------");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println(
                        "Job ID: " + rs.getInt("job_id") + " | " +
                        rs.getString("company_name") + " | " +
                        rs.getString("job_title") + " | Package: " +
                        rs.getDouble("salary_package") + " LPA"
                );
            }

            if (!found) {
                System.out.println("No companies available matching your GPA and skills.");
                return;
            }

            int choice;
            do {

                System.out.println("\n1. Apply for Job");
                System.out.println("2. View My Applications");
                System.out.println("3. Logout");
                System.out.print("Enter choice: ");

                choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        applyForJob(con, studentId);
                        break;

                    case 2:
                        viewMyApplications(con, studentId);
                        break;

                    case 3:
                        System.out.println("Logging out...");
                        break;

                    default:
                        System.out.println("Invalid choice");
                }

            } while (choice != 3);

        } catch (SQLException e) {
            System.out.println("Error fetching jobs.");
        }
    }

    // ================= APPLY FOR JOB =================
    public static void applyForJob(Connection con, int studentId) {

        Scanner sc = new Scanner(System.in);

        try {

            PreparedStatement checkStatus = con.prepareStatement(
                    "SELECT status FROM students WHERE student_id=?");
            checkStatus.setInt(1, studentId);

            ResultSet statusRs = checkStatus.executeQuery();

            if (statusRs.next()) {

                if (statusRs.getString("status").equalsIgnoreCase("Placed")) {
                    System.out.println("You are already placed and cannot apply for jobs.");
                    return;
                }
            }

            System.out.print("Enter Job ID to apply: ");
            int jobId = sc.nextInt();

            PreparedStatement check = con.prepareStatement(
                    "SELECT * FROM applications WHERE student_id = ? AND job_id = ?");
            check.setInt(1, studentId);
            check.setInt(2, jobId);

            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                System.out.println("You have already applied for this job.");
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO applications (student_id, job_id, status, applied_date) VALUES (?, ?, 'Applied', CURDATE())");
            ps.setInt(1, studentId);
            ps.setInt(2, jobId);

            ps.executeUpdate();

            System.out.println("Application submitted successfully ✅");

        } catch (SQLException e) {
            System.out.println("Error applying for job.");
        }
    }

    // ================= VIEW MY APPLICATIONS =================
    public static void viewMyApplications(Connection con, int studentId) {

        try {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT a.application_id, j.company_name, j.job_title, a.status " +
                            "FROM applications a " +
                            "JOIN job_postings j ON a.job_id = j.job_id " +
                            "WHERE a.student_id = ?");
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nMy Applications:");
            System.out.println("----------------------------------------------------");

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println(
                        "Application ID: " + rs.getInt("application_id") +
                                " | Company: " + rs.getString("company_name") +
                                " | Role: " + rs.getString("job_title") +
                                " | Status: " + rs.getString("status")
                );
            }

            if (!found) {
                System.out.println("No applications found.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving applications.");
        }
    }

    // ================= ADMIN LOGIN =================
    public static void adminLogin() {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Admin Username: ");
        String username = sc.nextLine();

        System.out.print("Enter Admin Password: ");
        String password = sc.nextLine();

        if (username.equals("admin") && password.equals("admin123")) {
            System.out.println("Admin Login Successful ✅");
            adminMenu();
        } else {
            System.out.println("Invalid Admin Credentials ❌");
        }
    }

    // ================= ADMIN MENU =================
    public static void adminMenu() {

        Scanner sc = new Scanner(System.in);
        int choice;

        do {

            System.out.println("\n===== ADMIN PANEL =====");
            System.out.println("1. Add Job");
            System.out.println("2. Delete Job");
            System.out.println("3. View All Jobs");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    addJob();
                    break;

                case 2:
                    deleteJob();
                    break;

                case 3:
                    viewAllJobs();
                    break;

                case 4:
                    System.out.println("Exiting Admin Panel...");
                    break;

                default:
                    System.out.println("Invalid choice");
            }

        } while (choice != 4);
    }

    // ================= ADD JOB =================
    public static void addJob() {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Company Name: ");
        String company = sc.nextLine();

        System.out.print("Enter Job Title: ");
        String job = sc.nextLine();

        System.out.print("Enter Minimum CGPA: ");
        double minCgpa = sc.nextDouble();

        System.out.print("Enter Salary Package: ");
        double salary = sc.nextDouble();

        try {

            Connection con = DBConnection.connect();

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO job_postings (company_name, job_title, min_cgpa, salary_package) VALUES (?, ?, ?, ?)");

            ps.setString(1, company);
            ps.setString(2, job);
            ps.setDouble(3, minCgpa);
            ps.setDouble(4, salary);

            ps.executeUpdate();

            System.out.println("Job added successfully ✅");

            con.close();

        } catch (SQLException e) {
            System.out.println("Error adding job.");
        }
    }

    // ================= DELETE JOB =================
    public static void deleteJob() {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Job ID to delete: ");
        int jobId = sc.nextInt();

        try {

            Connection con = DBConnection.connect();

            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM job_postings WHERE job_id = ?");
            ps.setInt(1, jobId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Job deleted successfully ✅");
            } else {
                System.out.println("Job not found ❌");
            }

            con.close();

        } catch (SQLException e) {
            System.out.println("Error deleting job.");
        }
    }

    // ================= VIEW ALL JOBS =================
    public static void viewAllJobs() {

        try {

            Connection con = DBConnection.connect();

            PreparedStatement ps = con.prepareStatement("SELECT * FROM job_postings");
            ResultSet rs = ps.executeQuery();

            System.out.println("\nAll Jobs:");
            System.out.println("----------------------------------------------------");

            while (rs.next()) {

                System.out.println(
                        "Job ID: " + rs.getInt("job_id") + " | " +
                                rs.getString("company_name") + " | " +
                                rs.getString("job_title") + " | Min CGPA: " +
                                rs.getDouble("min_cgpa") + " | Package: " +
                                rs.getDouble("salary_package") + " LPA"
                );
            }

            con.close();

        } catch (SQLException e) {
            System.out.println("Error fetching jobs.");
        }
    }
}