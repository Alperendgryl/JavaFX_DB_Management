package db.management.advanced.java;

import java.util.Date;

public class Student {

    String ssn;
    String FName;
    String mi;
    String LName;
    Date BDate;
    String street;
    String phone;
    String zipCode;
    String deptId;

    public Student(String ssn, String fName, String mi, String LName, Date bDate, String street, String phone, String zipCode, String deptId) {
        this.ssn = ssn;
        this.FName = fName;
        this.mi = mi;
        this.LName = LName;
        this.BDate = bDate;
        this.street = street;
        this.phone = phone;
        this.zipCode = zipCode;
        this.deptId = deptId;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getMi() {
        return mi;
    }

    public void setMi(String mi) {
        this.mi = mi;
    }

    public String getLName() {
        return LName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public Date getBDate() {
        return BDate;
    }

    public void setBDate(Date BDate) {
        this.BDate = BDate;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    @Override
    public String toString() {
        return "Student{" + "ssn=" + ssn + ", FName=" + FName + ", mi=" + mi + ", LName=" + LName + ", BDate=" + BDate + ", street=" + street + ", phone=" + phone + ", zipCode=" + zipCode + ", deptId=" + deptId + '}';
    }

    
}
