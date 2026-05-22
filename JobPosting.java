public class JobPosting {

    private int jobId;
    private String companyName;
    private String jobTitle;
    private double minCgpa;
    private double salary;

    public JobPosting(int jobId, String companyName,
                      String jobTitle, double minCgpa, double salary) {
        this.jobId = jobId;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.minCgpa = minCgpa;
        this.salary = salary;
    }

    public int getJobId() { return jobId; }
    public String getCompanyName() { return companyName; }
    public String getJobTitle() { return jobTitle; }
    public double getMinCgpa() { return minCgpa; }
    public double getSalary() { return salary; }
}