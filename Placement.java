import java.time.LocalDate;

public class Placement {

    private int studentId;
    private int jobId;
    private LocalDate placedDate;
    private double salary;
    private String designation;

    public Placement(int studentId, int jobId,
                     LocalDate placedDate, double salary, String designation) {
        this.studentId = studentId;
        this.jobId = jobId;
        this.placedDate = placedDate;
        this.salary = salary;
        this.designation = designation;
    }

    public int getStudentId() { return studentId; }
    public int getJobId() { return jobId; }
    public LocalDate getPlacedDate() { return placedDate; }
    public double getSalary() { return salary; }
    public String getDesignation() { return designation; }
}