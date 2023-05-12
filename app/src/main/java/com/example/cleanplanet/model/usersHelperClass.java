package com.example.cleanplanet.model;

public class usersHelperClass {
    String userName;
    String dateTimeRegistration;
    Boolean isActive;

    public usersHelperClass(String userName, String dateTimeRegistration, Boolean isActive) {
        this.userName = userName;
        this.dateTimeRegistration = dateTimeRegistration;
        this.isActive = isActive;
    }

    public usersHelperClass() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDateTimeRegistration() {
        return dateTimeRegistration;
    }

    public void setDateTimeRegistration(String dateTimeRegistration) {
        this.dateTimeRegistration = dateTimeRegistration;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "usersHelperClass{" +
                "userName='" + userName + '\'' +
                ", dateTimeRegistration='" + dateTimeRegistration + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
