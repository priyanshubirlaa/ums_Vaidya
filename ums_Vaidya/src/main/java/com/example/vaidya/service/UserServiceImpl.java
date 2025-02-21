package com.example.vaidya.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.vaidya.entity.User;
import com.example.vaidya.exception.UserNotFoundException;
import com.example.vaidya.repo.UserRepo;
import com.example.vaidya.specification.UserSpecification;

/**
 * 📌 UserServiceImpl - Implementation of UserService
 * - Provides CRUD operations for users.
 * - Supports filtering, pagination, and exception handling.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepository;

    /**
     * ✅ Retrieve all users from the database.
     * @return List of all User entities.
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * ✅ Retrieve a user by ID.
     * @param id The unique identifier of the user.
     * @return An Optional containing the User if found.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id)));
    }

    /**
     * ✅ Update an existing user.
     * @param id The unique identifier of the user.
     * @param userDetails The updated user details.
     * @return The updated User entity.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    @Override
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setFullName(userDetails.getFullName());
            user.setUserEmail(userDetails.getUserEmail());
            user.setSpecialization(userDetails.getSpecialization());
            user.setQualification(userDetails.getQualification());
            user.setExperience(userDetails.getExperience());
            user.setAddress(userDetails.getAddress());
            user.setPassword(userDetails.getPassword());
            user.setEnabled(userDetails.isEnabled());
            user.setRoleId(userDetails.getRoleId());
            user.setAadharNo(userDetails.getAadharNo());
            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    /**
     * ✅ Delete a user by ID.
     * @param id The unique identifier of the user.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * ✅ Retrieve a paginated list of users.
     * @param page The page number (zero-based index).
     * @param size The number of records per page.
     * @return A paginated list of users.
     */
    @Override
    public Page<User> getUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * ✅ Filter users based on dynamic criteria.
     * @param filters A map containing key-value pairs for filtering users.
     * @return A list of filtered User entities.
     */
    @Override
    public List<User> filterUserss(Map<String, Object> filters) {
        Specification<User> specification = UserSpecification.getSpecification(filters);
        return userRepository.findAll(specification);
    }
    
    @Override
    public List<User> getAllUsersSorted(String sortBy, String sortDirection) {
        if (!List.of("fullName", "userEmail", "roleId", "gender").contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sorting field: " + sortBy);
        }

        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        return userRepository.findAll(sort);
    }

    /**
     * ❌ Commented Alternative Filtering Method (Replaced by Dynamic Filtering)
     * - Used multiple conditional checks to apply different filter combinations.
     * - Replaced with a more flexible approach using Specifications.
     */
}
