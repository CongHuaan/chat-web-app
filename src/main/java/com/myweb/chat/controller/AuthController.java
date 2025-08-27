package com.myweb.chat.controller;

import com.myweb.chat.dto.AuthResponse;
import com.myweb.chat.dto.LoginRequest;
import com.myweb.chat.dto.RegisterRequest;
import com.myweb.chat.model.User;
import com.myweb.chat.security.JwtUtil;
import com.myweb.chat.service.AuthService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    // Đăng ký user thường
    @Operation(summary = "Đăng ký người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng ký thành công"),
            @ApiResponse(responseCode = "400", description = "Tên đăng nhập đã tồn tại")
    })
    @PostMapping("/signup")
    public String signup(@RequestBody RegisterRequest req) {
        authService.register(req, false);
        return "Đăng ký người dùng thành công";
    }

    // Đăng ký admin
    @Operation(summary = "Đăng ký quản trị viên")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng ký thành công"),
            @ApiResponse(responseCode = "400", description = "Tên đăng nhập đã tồn tại")
    })
    @PostMapping("/signup/admin")
    public String signupAdmin(@RequestBody RegisterRequest req) {
        authService.register(req, true);
        return "Đăng ký quản trị viên thành công";
    }

    // Đăng nhập
    @Operation(summary = "Đăng nhập")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
            @ApiResponse(responseCode = "401", description = "Mật khẩu không đúng"),
            @ApiResponse(responseCode = "404", description = "Người dùng không tồn tại")
    })
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        User u = authService.login(req.getUsername(), req.getPassword());

        Set<String> roles = u.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        String token = jwtUtil.generateToken(u.getUsername(), roles);

        return new AuthResponse(u.getUsername(), roles, token);
    }
}
