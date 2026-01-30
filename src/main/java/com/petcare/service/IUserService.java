package com.petcare.service;

import com.petcare.model.domain.User;
import com.petcare.model.exception.PetcareException;

import java.util.List;

public interface IUserService {
    User authenticate(String username, String plainPassword) throws PetcareException;
    List<User> getAllUsers() throws PetcareException;
    User getUserById(int id) throws PetcareException;
    void createUser(User user, String plainPassword, User currentUser) throws PetcareException;
    void updateUser(User user, User currentUser) throws PetcareException;
    void deleteUser(int id, User currentUser) throws PetcareException;
    void changePassword(int userId, String newPlainPassword, User currentUser) throws PetcareException;
    boolean existsByUsername(String username) throws PetcareException;
}
