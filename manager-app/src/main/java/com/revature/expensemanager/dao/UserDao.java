package com.revature.expensemanager.dao;

import java.util.Optional;

import com.revature.expensemanager.model.User;

public interface UserDAO {

    Optional<User> findByUsername(String username);

    Optional<User> findById(int id);
}
