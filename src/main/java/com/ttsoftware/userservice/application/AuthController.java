package com.ttsoftware.userservice.application;

import com.ttsoftware.userservice.application.mapper.UserMapper;
import com.ttsoftware.userservice.domain.dto.*;
import com.ttsoftware.userservice.domain.entity.User;
import com.ttsoftware.userservice.model.JwtResponse;
import com.ttsoftware.userservice.model.SignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(@RequestBody LoginDto loginDto){
            return authService.loginService(loginDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signupUser(@RequestBody SignupDto signupDto){
        return authService.signupUserService(signupDto);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto){
        return authService.changePasswordService(changePasswordDto);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto){
        return authService.forgotPassword(forgotPasswordDto.getEmail());
    }

    @PostMapping("/validateToken")
    public ResponseEntity<Boolean> validateToken(@RequestBody ValidateTokenDto token) {
        return authService.validateToken(token.getToken());
    }

    @PostMapping("/getUserByEmail")
    public ResponseEntity<Long> getUserIDByEmail(@RequestBody EmailQueryRequest email) {
        return authService.getUserIDByEmail(email);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return authService.getAllUser();
    }
}