package db.management.advanced.java;

import java.util.Date;

public class Enrollment {

    String ssn;
    String courseID;
    Date dateReg;
    String grade;

    public Enrollment(String ssn, String courseID, Date dateReg, String grade) {
        this.ssn = ssn;
        this.courseID = courseID;
        this.dateReg = dateReg;
        this.grade = grade;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public Date getDateReg() {
        return dateReg;
    }

    public void setDateReg(Date dateReg) {
        this.dateReg = dateReg;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Enrollment{" + "ssn=" + ssn + ", courseID=" + courseID + ", dateReg=" + dateReg + ", grade=" + grade + '}';
    }
    
    

}
