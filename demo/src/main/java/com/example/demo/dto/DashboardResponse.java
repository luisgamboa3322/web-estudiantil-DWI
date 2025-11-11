package com.example.demo.dto;

import java.util.List;

public class DashboardResponse {
    private String userType;
    private String userName;
    private String userEmail;
    private List<CourseDto> courses;
    private int totalCourses;
    private boolean hasMultipleRoles;
    private String avatarUrl;

    public DashboardResponse() {}

    public DashboardResponse(String userType, String userName, String userEmail, 
                           List<CourseDto> courses, boolean hasMultipleRoles) {
        this.userType = userType;
        this.userName = userName;
        this.userEmail = userEmail;
        this.courses = courses;
        this.hasMultipleRoles = hasMultipleRoles;
        this.totalCourses = courses != null ? courses.size() : 0;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<CourseDto> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseDto> courses) {
        this.courses = courses;
    }

    public int getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(int totalCourses) {
        this.totalCourses = totalCourses;
    }

    public boolean isHasMultipleRoles() {
        return hasMultipleRoles;
    }

    public void setHasMultipleRoles(boolean hasMultipleRoles) {
        this.hasMultipleRoles = hasMultipleRoles;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}