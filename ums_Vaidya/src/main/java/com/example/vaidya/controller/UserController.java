package com.example.vaidya.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.vaidya.entity.ErrorResponse;
import com.example.vaidya.entity.User;
import com.example.vaidya.exception.UserNotFoundException;
import com.example.vaidya.service.UserService;

/**
 * 📌 User Controller - Manages User API Endpoints.
 * - Provides CRUD operations on User entities.
 * - Implements pagination and filtering.
 * - Handles exceptions gracefully with structured error responses.
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * ✅ Get All Users
     * - Fetches all users from the database.
     * - Returns 204 (No Content) if no users are found.
     *
     * @return List of users or error response.
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return buildErrorResponse("No users found.", HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(users);
    }

    /**
     * ✅ Get User by ID
     * - Fetches a user based on their ID.
     * - Returns 404 (Not Found) if the user doesn't exist.
     *
     * @param id User ID
     * @return User details or error response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return buildErrorResponse("User not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * ✅ Update User
     * - Updates user details based on provided data.
     * - Throws 404 (Not Found) if the user doesn't exist.
     * - Handles unexpected errors gracefully.
     *
     * @param id          User ID
     * @param userDetails Updated user details
     * @return Updated user details or error response.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error updating user.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ✅ Delete User
     * - Deletes a user from the database.
     * - Returns 404 (Not Found) if the user doesn't exist.
     *
     * @param id User ID
     * @return Success message or error response.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (UserNotFoundException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error deleting user.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ✅ Pagination - Get Users in Pages
     * - Supports pagination for fetching users efficiently.
     * - Returns 204 (No Content) if no users exist for the given page.
     *
     * @param page Page number (default = 0)
     * @param size Number of users per page (default = 10)
     * @return Paginated user data or error response.
     */
    @GetMapping("/pagination")
    public ResponseEntity<?> getUsers(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = userService.getUsers(page, size);
        if (userPage.isEmpty()) {
            return buildErrorResponse("No users found for the given page.", HttpStatus.NO_CONTENT);
        }

        // Construct structured response with pagination details
        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("totalPages", userPage.getTotalPages());
        response.put("currentPage", userPage.getNumber());

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ User Filtering
     * - Allows filtering users based on various criteria.
     * - Returns 204 (No Content) if no matching users are found.
     * - Handles unexpected filtering errors gracefully.
     *
     * @param filters Map of filter criteria
     * @return Filtered user list or error response.
     */
    @GetMapping("/filter")
    public ResponseEntity<?> filterUsers(@RequestParam Map<String, Object> filters) {
        try {
            List<User> users = userService.filterUserss(filters);
            if (users.isEmpty()) {
                return buildErrorResponse("No users found matching the filters.", HttpStatus.NO_CONTENT);
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return buildErrorResponse("Error filtering users.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 🔹 Helper Method to Build Structured Error Responses.
     * - Provides a consistent error format across the application.
     *
     * @param message Error message
     * @param status  HTTP status code
     * @return Structured error response.
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message
                ),
                status
        );
    }
    
    
    @GetMapping("/sorted")
    public List<User> getSortedUsers(
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        return userService.getAllUsersSorted(sortBy, sortDirection);
    }
}
