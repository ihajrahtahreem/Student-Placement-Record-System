import java.awt.*;
import java.io.File;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PlacementGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createMainMenu());
    }

    // ================= MAIN MENU =================
    public static void createMainMenu() {

        JFrame frame = new JFrame("Placement System");
        frame.setSize(700,450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(40,40,40,40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15,15,15,15);

        JLabel college = new JLabel("Muffakham Jah College of Engineering and Technology");
        college.setFont(new Font("Serif",Font.BOLD,22));

        JLabel system = new JLabel("Student Placement Record System");
        system.setFont(new Font("Arial",Font.PLAIN,18));

        JButton studentBtn = new JButton("Student Login");
        JButton adminBtn = new JButton("Admin Login");

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        panel.add(college,gbc);

        gbc.gridy=1;
        panel.add(system,gbc);

        JPanel buttonPanel=new JPanel();
        buttonPanel.add(studentBtn);
        buttonPanel.add(adminBtn);

        gbc.gridy=2;
        panel.add(buttonPanel,gbc);

        studentBtn.addActionListener(e -> openStudentLogin());
        adminBtn.addActionListener(e -> openAdminLogin());

        frame.add(panel);
        frame.setVisible(true);
    }

    // ================= STUDENT LOGIN =================
    public static void openStudentLogin(){

        JFrame frame=new JFrame("Student Login");
        frame.setSize(400,250);
        frame.setLocationRelativeTo(null);

        JPanel panel=new JPanel(new GridLayout(3,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

        JTextField idField=new JTextField();
        JPasswordField passField=new JPasswordField();
        JButton login=new JButton("Login");

        panel.add(new JLabel("Student ID:"));
        panel.add(idField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(new JLabel());
        panel.add(login);

        login.addActionListener(e->{

            try{

                int id=Integer.parseInt(idField.getText());
                String pass=new String(passField.getPassword());

                Connection con=DBConnection.connect();

                PreparedStatement ps=con.prepareStatement(
                        "SELECT * FROM students WHERE student_id=? AND password=?");

                ps.setInt(1,id);
                ps.setString(2,pass);

                ResultSet rs=ps.executeQuery();

                if(rs.next()){

                    double gpa = rs.getDouble("gpa");
                    String status = rs.getString("status");

                    frame.dispose();
                    openStudentDashboard(id, gpa, status);

                }else{

                    JOptionPane.showMessageDialog(frame,"Invalid Credentials");

                }

                con.close();

            }catch(Exception ex){

                JOptionPane.showMessageDialog(frame,"Error: "+ex.getMessage());

            }

        });

        frame.add(panel);
        frame.setVisible(true);
    }

    // ================= STUDENT DASHBOARD =================
    public static void openStudentDashboard(int studentId,double gpa,String status){

        JFrame frame=new JFrame("Student Dashboard");
        frame.setSize(550,400);
        frame.setLocationRelativeTo(null);

        JPanel panel=new JPanel(new GridLayout(4,1,15,15));
        panel.setBorder(BorderFactory.createEmptyBorder(40,80,40,80));

        JButton view=new JButton("View Eligible Jobs");
        JButton apply=new JButton("Apply for Job");
        JButton apps=new JButton("View My Applications");
        JButton logout=new JButton("Logout");

        panel.add(view);
        panel.add(apply);
        panel.add(apps);
        panel.add(logout);

        view.addActionListener(e -> {

    if(status.equalsIgnoreCase("Placed")){
        JOptionPane.showMessageDialog(null,
                "You are already placed. No more jobs available.");
    } else {
        showEligibleJobsGUI(gpa);
    }
        });
    
        apply.addActionListener(e->applyForJobGUI(studentId));
        apps.addActionListener(e->viewApplicationsGUI(studentId));
        logout.addActionListener(e->frame.dispose());

        frame.add(panel);
        frame.setVisible(true);
    }

    // ================= SHOW JOBS =================
    public static void showEligibleJobsGUI(double gpa){

        try{

            Connection con=DBConnection.connect();

            PreparedStatement ps=con.prepareStatement(
                    "SELECT job_id,company_name,job_title,salary_package FROM job_postings WHERE min_cgpa<=?");

            ps.setDouble(1,gpa);

            ResultSet rs=ps.executeQuery();

            String[] cols={"Job ID","Company","Role","Package"};
            DefaultTableModel model=new DefaultTableModel(cols,0);

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt("job_id"),
                        rs.getString("company_name"),
                        rs.getString("job_title"),
                        rs.getDouble("salary_package")
                });

            }

            JTable table=new JTable(model);

            JFrame f=new JFrame("Eligible Jobs");
            f.setSize(750,400);
            f.add(new JScrollPane(table));
            f.setLocationRelativeTo(null);
            f.setVisible(true);

            con.close();

        }catch(Exception e){

            JOptionPane.showMessageDialog(null,"Error Loading Jobs");

        }
    }

    // ================= APPLY =================
    public static void applyForJobGUI(int studentId){

        JTextField jobField=new JTextField();
        Object[] message={"Job ID:",jobField};

        int option=JOptionPane.showConfirmDialog(null,message,
                "Apply For Job",JOptionPane.OK_CANCEL_OPTION);

        if(option!=JOptionPane.OK_OPTION) return;

        try{

            int jobId=Integer.parseInt(jobField.getText());

            Connection con=DBConnection.connect();

            PreparedStatement ps=con.prepareStatement(
                    "INSERT INTO applications(student_id,job_id,status,applied_date) VALUES (?, ?, 'Applied', CURDATE())");

            ps.setInt(1,studentId);
            ps.setInt(2,jobId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(null,"Application Submitted!");

            con.close();

        }catch(Exception e){

            JOptionPane.showMessageDialog(null,"Invalid Job ID");

        }
    }

    // ================= VIEW APPLICATIONS =================
    public static void viewApplicationsGUI(int studentId){

        try{

            Connection con=DBConnection.connect();

            PreparedStatement ps=con.prepareStatement(
                    "SELECT a.application_id,j.company_name,j.job_title,a.status " +
                            "FROM applications a JOIN job_postings j ON a.job_id=j.job_id WHERE a.student_id=?");

            ps.setInt(1,studentId);

            ResultSet rs=ps.executeQuery();

            String[] cols={"App ID","Company","Role","Status"};
            DefaultTableModel model=new DefaultTableModel(cols,0);

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt("application_id"),
                        rs.getString("company_name"),
                        rs.getString("job_title"),
                        rs.getString("status")
                });

            }

            JTable table=new JTable(model);

            JFrame f=new JFrame("My Applications");
            f.setSize(750,400);
            f.add(new JScrollPane(table));
            f.setLocationRelativeTo(null);
            f.setVisible(true);

            con.close();

        }catch(Exception e){

            JOptionPane.showMessageDialog(null,"Error Loading Applications");

        }
    }

    // ================= ADMIN LOGIN =================
    public static void openAdminLogin(){

        JFrame frame=new JFrame("Admin Login");
        frame.setSize(400,250);
        frame.setLocationRelativeTo(null);

        JPanel panel=new JPanel(new GridLayout(3,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

        JTextField user=new JTextField();
        JPasswordField pass=new JPasswordField();
        JButton login=new JButton("Login");

        panel.add(new JLabel("Username:"));
        panel.add(user);
        panel.add(new JLabel("Password:"));
        panel.add(pass);
        panel.add(new JLabel());
        panel.add(login);

        login.addActionListener(e->{

            if(user.getText().equals("admin") &&
                    new String(pass.getPassword()).equals("admin123")){

                frame.dispose();
                openAdminDashboard();

            }else{

                JOptionPane.showMessageDialog(frame,"Invalid Credentials");

            }

        });

        frame.add(panel);
        frame.setVisible(true);
    }

    // ================= ADMIN DASHBOARD =================
    public static void openAdminDashboard(){

        JFrame frame=new JFrame("Admin Dashboard");
        frame.setSize(600,600);
        frame.setLocationRelativeTo(null);

        JPanel panel=new JPanel(new GridLayout(7,1,15,15));
        panel.setBorder(BorderFactory.createEmptyBorder(40,80,40,80));

        JButton viewStudents=new JButton("View All Students");
        JButton viewJobs=new JButton("View All Jobs");
        JButton viewApps=new JButton("View All Applications");
        JButton updateStatus=new JButton("Update Application Status");
        JButton stats=new JButton("Placement Statistics");
        JButton export=new JButton("Export Placement Report");
        JButton logout=new JButton("Logout");

        panel.add(viewStudents);
        panel.add(viewJobs);
        panel.add(viewApps);
        panel.add(updateStatus);
        panel.add(stats);
        panel.add(export);
        panel.add(logout);

        viewStudents.addActionListener(e -> viewAllStudentsGUI());

        viewJobs.addActionListener(e -> viewAllJobsGUI());

        viewApps.addActionListener(e -> viewAllApplicationsGUI());

        updateStatus.addActionListener(e -> updateApplicationStatusGUI());

        stats.addActionListener(e -> showPlacementStatistics());

        export.addActionListener(e -> exportPlacementReport());

        logout.addActionListener(e -> frame.dispose());

        logout.addActionListener(e->frame.dispose());

        frame.add(panel);
        frame.setVisible(true);
    }
    public static void viewAllStudentsGUI() {

    try {

        Connection con = DBConnection.connect();

        PreparedStatement ps = con.prepareStatement(
                "SELECT student_id,name,department,gpa,skills,status,resume_path FROM students");

        ResultSet rs = ps.executeQuery();

        String[] cols = {"ID","Name","Dept","GPA","Skills","Status","Resume"};
        DefaultTableModel model = new DefaultTableModel(cols,0);

        while(rs.next()){

            model.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getDouble("gpa"),
                    rs.getString("skills"),
                    rs.getString("status"),
                    rs.getString("resume_path")
            });

        }

        JTable table = new JTable(model);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {

        int column = table.getSelectedColumn();
        int row = table.getSelectedRow();

        // Resume column index (based on your table it is 6)
        if(column == 6){

            String path = table.getValueAt(row, column).toString();

            try{

                File file = new File("." + path); // because path is /resumes/...
                Desktop.getDesktop().open(file);

            }catch(Exception e){

                JOptionPane.showMessageDialog(null,
                        "Could not open resume file.");

            }
        }
    }
});

        JFrame frame = new JFrame("All Students");
        frame.setSize(800,400);
        frame.add(new JScrollPane(table));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        con.close();

    } catch(Exception e){

        JOptionPane.showMessageDialog(null,"Error Loading Students");

    }
}
public static void viewAllJobsGUI() {

    try {

        Connection con = DBConnection.connect();

        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM job_postings");

        ResultSet rs = ps.executeQuery();

        String[] cols = {"Job ID","Company","Role","Min CGPA","Package"};
        DefaultTableModel model = new DefaultTableModel(cols,0);

        while(rs.next()){

            model.addRow(new Object[]{
                    rs.getInt("job_id"),
                    rs.getString("company_name"),
                    rs.getString("job_title"),
                    rs.getDouble("min_cgpa"),
                    rs.getDouble("salary_package")
            });

        }

        JTable table = new JTable(model);

        JFrame frame = new JFrame("All Jobs");
        frame.setSize(800,400);
        frame.add(new JScrollPane(table));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        con.close();

    } catch(Exception e){

        JOptionPane.showMessageDialog(null,"Error Loading Jobs");

    }
}
public static void viewAllApplicationsGUI() {

    try {

        Connection con = DBConnection.connect();

        PreparedStatement ps = con.prepareStatement(
                "SELECT a.application_id,s.name,j.company_name,j.job_title,a.status " +
                "FROM applications a " +
                "JOIN students s ON a.student_id=s.student_id " +
                "JOIN job_postings j ON a.job_id=j.job_id");

        ResultSet rs = ps.executeQuery();

        String[] cols = {"App ID","Student","Company","Role","Status"};
        DefaultTableModel model = new DefaultTableModel(cols,0);

        while(rs.next()){

            model.addRow(new Object[]{
                    rs.getInt("application_id"),
                    rs.getString("name"),
                    rs.getString("company_name"),
                    rs.getString("job_title"),
                    rs.getString("status")
            });

        }

        JTable table = new JTable(model);

        JFrame frame = new JFrame("All Applications");
        frame.setSize(900,450);
        frame.add(new JScrollPane(table));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        con.close();

    } catch(Exception e){

        JOptionPane.showMessageDialog(null,"Error Loading Applications");

    }
}
public static void updateApplicationStatusGUI() {

    JTextField appIdField = new JTextField();

    String[] statuses = {"Applied","Shortlisted","Interviewed","Selected","Rejected"};

    JComboBox<String> statusBox = new JComboBox<>(statuses);

    Object[] message = {
            "Application ID:", appIdField,
            "New Status:", statusBox
    };

    int option = JOptionPane.showConfirmDialog(
            null,message,"Update Status",JOptionPane.OK_CANCEL_OPTION);

    if(option != JOptionPane.OK_OPTION) return;

    try {

        int appId = Integer.parseInt(appIdField.getText());
        String newStatus = (String) statusBox.getSelectedItem();

        Connection con = DBConnection.connect();

        PreparedStatement ps = con.prepareStatement(
                "UPDATE applications SET status=? WHERE application_id=?");

        ps.setString(1,newStatus);
        ps.setInt(2,appId);

        ps.executeUpdate();

        JOptionPane.showMessageDialog(null,"Status Updated!");

        con.close();

    } catch(Exception e){

        JOptionPane.showMessageDialog(null,"Error Updating Status");

    }
}
public static void showPlacementStatistics(){

    try{

        Connection con = DBConnection.connect();
        Statement st = con.createStatement();

        ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM students");
        rs1.next();
        int totalStudents = rs1.getInt(1);

        ResultSet rs2 = st.executeQuery(
                "SELECT COUNT(DISTINCT student_id) FROM applications WHERE status='Selected'");
        rs2.next();
        int placed = rs2.getInt(1);

        ResultSet rs3 = st.executeQuery("SELECT COUNT(*) FROM applications");
        rs3.next();
        int totalApps = rs3.getInt(1);

        con.close();

        JFrame frame = new JFrame("Placement Statistics");
        frame.setSize(700,500);
        frame.setLocationRelativeTo(null);

        frame.add(new StatsPanel(totalStudents,placed,totalApps));

        frame.setVisible(true);

    } catch(Exception e){

        JOptionPane.showMessageDialog(null,"Error Loading Statistics");

    }
}
public static void exportPlacementReport(){

    try{

        Connection con = DBConnection.connect();

        PreparedStatement ps = con.prepareStatement(
                "SELECT s.name,s.department,j.company_name,j.salary_package " +
                "FROM applications a " +
                "JOIN students s ON a.student_id=s.student_id " +
                "JOIN job_postings j ON a.job_id=j.job_id " +
                "WHERE a.status='Selected'");

        ResultSet rs = ps.executeQuery();

        java.io.FileWriter writer = new java.io.FileWriter("Placement_Report.txt");

        writer.write("===== PLACEMENT REPORT =====\n\n");

        while(rs.next()){

            writer.write(
                    "Name: "+rs.getString("name")+
                    " | Department: "+rs.getString("department")+
                    " | Company: "+rs.getString("company_name")+
                    " | Package: "+rs.getDouble("salary_package")+" LPA\n");

        }

        writer.close();
        con.close();

        JOptionPane.showMessageDialog(null,
                "Placement_Report.txt generated successfully!");

    } catch(Exception e){

        JOptionPane.showMessageDialog(null,"Error generating report.");

    }
}
}
class StatsPanel extends JPanel {

    int totalStudents;
    int selectedStudents;
    int totalApplications;

    public StatsPanel(int totalStudents, int selectedStudents, int totalApplications) {

        this.totalStudents = totalStudents;
        this.selectedStudents = selectedStudents;
        this.totalApplications = totalApplications;

    }

    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        int baseY = 350;
        int barWidth = 100;

        int maxValue = Math.max(
                totalStudents,
                Math.max(selectedStudents, totalApplications));

        double scale = 250.0 / (maxValue == 0 ? 1 : maxValue);

        g.setColor(Color.BLUE);
        g.fillRect(120, baseY - (int) (totalStudents * scale),
                barWidth,
                (int) (totalStudents * scale));

        g.drawString("Students: " + totalStudents, 120, baseY + 20);

        g.setColor(Color.GREEN);
        g.fillRect(300, baseY - (int) (selectedStudents * scale),
                barWidth,
                (int) (selectedStudents * scale));

        g.drawString("Selected: " + selectedStudents, 300, baseY + 20);

        g.setColor(Color.ORANGE);
        g.fillRect(480, baseY - (int) (totalApplications * scale),
                barWidth,
                (int) (totalApplications * scale));

        g.drawString("Applications: " + totalApplications, 480, baseY + 20);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Placement Statistics Overview", 240, 40);
    }
}