package com.petcare.repository;

import com.petcare.model.entity.UserEntity;
import com.petcare.model.exception.PetcareException;

import java.util.List;

/**
 * User Repository Interface
 * Data access operations for User entity
 */
public interface IUserRepository {

    List<UserEntity> findAll() throws PetcareException;

    UserEntity findById(int id) throws PetcareException;

    UserEntity findByUsernameAndPassword(String username, String hashedPassword) throws PetcareException;

    int insert(UserEntity entity) throws PetcareException;

    int update(UserEntity entity) throws PetcareException;

    int updatePassword(int id, String hashedPassword) throws PetcareException;

    int delete(int id) throws PetcareException;

    boolean existsByUsername(String username) throws PetcareException;

    /**
     * Check username exists excluding given id (for update).
     */
    boolean existsByUsernameExcludingId(String username, int excludeId) throws PetcareException;
}
