package com.example.capstone2.repository;

import com.example.capstone2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserById(Integer id);

    User findUserByEmailAndPassword(String email, String password);

    User findUserByEmail(String email);


    List<User> findUsersByRole(String role);   // CLIENT or VENDOR
}