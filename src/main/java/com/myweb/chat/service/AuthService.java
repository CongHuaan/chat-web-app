package com.myweb.chat.service;

import com.myweb.chat.dto.RegisterRequest;
import com.myweb.chat.model.Role;
import com.myweb.chat.model.User;
import com.myweb.chat.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class
AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    // Đăng ký user mới
    public User register(RegisterRequest req, boolean isAdmin) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên đăng nhập đã tồn tại");
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(encoder.encode(req.getPassword())); // dùng BCrypt
        u.setRoles(isAdmin ? Set.of(Role.ROLE_ADMIN) : Set.of(Role.ROLE_USER));
        return userRepo.save(u);
    }

    // Xác thực đăng nhập
    public User login(String username, String rawPassword) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Người dùng không tồn tại"));

        if (!encoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mật khẩu không đúng");
        }
        return user;
    }
}
