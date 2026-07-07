package com.revature.expensemanager.service;

import java.util.Optional;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revature.expensemanager.dao.UserDAO;
import com.revature.expensemanager.dto.LoginRequest;
import com.revature.expensemanager.dto.LoginResponse;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<LoginResponse> login(LoginRequest loginRequest) {
        if (loginRequest == null) {
            logger.warn("Login failed: request body was null.");
            return Optional.empty();
        }

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            logger.warn("Login failed: username or password was blank.");
            return Optional.empty();
        }

        logger.info("Authenticating manager login for username={}", username);

        Optional<LoginResponse> response = userDAO.findByUsername(username)
                .filter(user -> BCrypt.verifyer()
                        .verify(password.toCharArray(), user.getPassword()).verified)
                .filter(user -> user.getRole().equalsIgnoreCase("manager"))
                .map(user -> new LoginResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole()));

        if (response.isPresent()) {
            logger.info("Manager authenticated successfully: username={}", username);
        } else {
            logger.warn("Manager authentication failed for username={}", username);
        }

        return response;
    }
}



// package com.revature.expensemanager.service;

// import java.util.Optional;

// import org.mindrot.jbcrypt.BCrypt;

// import com.revature.expensemanager.dao.UserDAO;
// import com.revature.expensemanager.dto.LoginRequest;
// import com.revature.expensemanager.dto.LoginResponse;

// public class AuthService {
//     private final UserDAO userDAO;

//     public AuthService(UserDAO userDAO) {
//         this.userDAO = userDAO;
//     }

//     public Optional<LoginResponse> login(LoginRequest loginRequest) {
//         if (loginRequest == null) {
//             return Optional.empty();
//         }

//         String username = loginRequest.getUsername();
//         String password = loginRequest.getPassword();

//         if (username == null || username.isBlank()
//                 || password == null || password.isBlank()) {
//             return Optional.empty();
//         }

//         return userDAO.findByUsername(username)
//                 .filter(user -> {
//                     String storedHash = user.getPassword();

//                     if (storedHash == null || storedHash.isBlank()) {
//                         return false;
//                     }

//                     try {
//                         return BCrypt.checkpw(password, storedHash.trim());
//                     } catch (Exception e) {
//                         System.out.println("BCrypt error for user: " + username);
//                         e.printStackTrace();
//                         return false;
//                     }
//                 })
//                 .filter(user -> user.getRole().equalsIgnoreCase("manager"))
//                 .map(user -> new LoginResponse(
//                         user.getId(),
//                         user.getUsername(),
//                         user.getRole()));
//     }

//     // public Optional<LoginResponse> login(LoginRequest loginRequest) {
//     // if (loginRequest == null) {
//     // return Optional.empty();
//     // }

//     // String username = loginRequest.getUsername();
//     // String password = loginRequest.getPassword();

//     // if (username == null || username.isBlank()
//     // || password == null || password.isBlank()) {
//     // return Optional.empty();
//     // }

//     // return userDAO.findByUsername(username)
//     // .filter(user -> BCrypt.checkpw(password, user.getPassword()))
//     // .filter(user -> user.getRole().equalsIgnoreCase("manager"))
//     // .map(user -> new LoginResponse(
//     // user.getId(),
//     // user.getUsername(),
//     // user.getRole()));
//     // }
// }
