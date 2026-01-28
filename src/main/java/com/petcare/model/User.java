package com.petcare.model;

/**
 * User model representing admin/staff users
 */
public class User {
    private int id;
    private String username;
    private String fullname;
    private String avatar;
    private Role role;
    
    public enum Role {
        ADMIN, STAFF
    }
    
    public User() {
    }
    
    public User(int id, String username, String fullname, String avatar, Role role) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.avatar = avatar;
        this.role = role;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFullname() {
        return fullname;
    }
    
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
}
