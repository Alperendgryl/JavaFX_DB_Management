package db.management.advanced.java;

public class Course {

    String courseID;
    String subjectID;
    String courseNum;
    String title;
    String numCredit;

    public Course(String courseID, String subjectID, String courseNum, String title, String numCredit) {
        this.courseID = courseID;
        this.subjectID = subjectID;
        this.courseNum = courseNum;
        this.title = title;
        this.numCredit = numCredit;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(String courseNum) {
        this.courseNum = courseNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumCredit() {
        return numCredit;
    }

    public void setNumCredit(String numCredit) {
        this.numCredit = numCredit;
    }

    @Override
    public String toString() {
        return "Course{" + "courseID=" + courseID + ", subjectID=" + subjectID + ", courseNum=" + courseNum + ", title=" + title + ", numCredit=" + numCredit + '}';
    }

    
}
