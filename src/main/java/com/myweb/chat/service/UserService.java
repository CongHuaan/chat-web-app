package com.myweb.chat.service;

import com.myweb.chat.model.User;
import com.myweb.chat.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Lấy tất cả user
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // Tìm user theo id
    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id);
    }

    // Tìm user theo username
    public Optional<User> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    // Xóa user
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
}
