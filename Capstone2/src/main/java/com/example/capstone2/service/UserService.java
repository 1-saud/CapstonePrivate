package com.example.capstone2.service;


import com.example.capstone2.model.User;
import com.example.capstone2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findUserById(id);
    }

    public boolean addUser(User user) {
        if (user == null) {
            return false;
        }

        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }

    public boolean updateUser(Integer id, User user) {
        User oldUser = userRepository.findUserById(id);
        if (oldUser == null) {
            return false;
        }
        oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());
        oldUser.setPassword(user.getPassword());
        oldUser.setRole(user.getRole());
        userRepository.save(oldUser);
        return true;
    }

    public boolean deleteUser(Integer id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            return false;
        }
        userRepository.delete(user);
        return true;
    }

    public List<User> getAllClients() {
        return userRepository.findUsersByRole("CLIENT");
    }

    public List<User> getAllVendors() {
        return userRepository.findUsersByRole("VENDOR");
    }


}
