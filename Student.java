public class Student {

    private int studentId;
    private String name;
    private String department;
    private double gpa;
    private String skills;
    private String status;

    public Student(int studentId, String name, String department,
                   double gpa, String skills, String status) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.gpa = gpa;
        this.skills = skills;
        this.status = status;
    }

    public int getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getGpa() { return gpa; }
    public String getSkills() { return skills; }
    public String getStatus() { return status; }
}