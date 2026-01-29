package com.petcare.model.domain;

import com.petcare.model.exception.PetcareException;

import java.util.Objects;

/**
 * User Domain Model
 * Contains validation rules for user data.
 * Used in business layer; no password stored here (security).
 */
public class User {
    private int userId;
    private String username;
    private String fullname;
    private String avatar;
    private Role role;

    public enum Role {
        ADMIN, STAFF
    }

    public User() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Alias for compatibility (e.g. DashboardFrame).
     */
    public int getId() {
        return userId;
    }

    public void setId(int id) {
        this.userId = id;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Set username with validation.
     */
    public final void setUsername(String username) throws PetcareException {
        if (username == null || username.trim().isEmpty()) {
            throw new PetcareException("Username không được để trống");
        }
        String u = username.trim();
        if (u.length() > 50) {
            throw new PetcareException("Username không được vượt quá 50 ký tự");
        }
        this.username = u;
    }

    public String getFullname() {
        return fullname;
    }

    /**
     * Set fullname with validation.
     */
    public final void setFullname(String fullname) throws PetcareException {
        if (fullname == null || fullname.trim().isEmpty()) {
            throw new PetcareException("Họ tên không được để trống");
        }
        if (fullname.trim().length() > 100) {
            throw new PetcareException("Họ tên không được vượt quá 100 ký tự");
        }
        this.fullname = fullname.trim();
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

    /**
     * Set role (ADMIN or STAFF).
     */
    public final void setRole(Role role) throws PetcareException {
        if (role == null) {
            throw new PetcareException("Vai trò không được để trống");
        }
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId || Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username);
    }
}
