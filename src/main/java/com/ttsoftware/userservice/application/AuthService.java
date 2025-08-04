package com.ttsoftware.userservice.application;

import com.ttsoftware.userservice.application.config.JwtTokenUtil;
import com.ttsoftware.userservice.client.CommunicationClient;
import com.ttsoftware.userservice.domain.dto.ChangePasswordDto;
import com.ttsoftware.userservice.domain.dto.EmailDtoWithAttachment;
import com.ttsoftware.userservice.domain.dto.LoginDto;
import com.ttsoftware.userservice.domain.dto.SignupDto;
import com.ttsoftware.userservice.domain.entity.User;
import com.ttsoftware.userservice.infrastructure.UserRepository;
import com.ttsoftware.userservice.model.JwtResponse;
import com.ttsoftware.userservice.model.SignupResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final CommunicationClient communicationClient;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    public ResponseEntity<JwtResponse> loginService(LoginDto loginDto) {
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            User user = userRepository.findByUsernameOrEmailAndChannelId(loginDto.getUsernameOrEmail(), loginDto.getUsernameOrEmail(),loginDto.getChannelId()).orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + loginDto.getUsernameOrEmail()));
            passwordEncoder.matches(loginDto.getPassword(), user.getPassword());

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            final UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginDto.getUsernameOrEmail());

            final String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new JwtResponse(e.getMessage()));
        }
    }


    public ResponseEntity<SignupResponse> signupUserService(SignupDto signupDto) {
        User user = new User();
        SignupResponse signupResponse = new SignupResponse();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        EmailDtoWithAttachment emailDtoWithAttachment = new EmailDtoWithAttachment();
        try {
            String encodedPassword = passwordEncoder.encode(signupDto.getPassword().trim());
            user.setEmail(signupDto.getEmail());
            user.setPassword(encodedPassword);
            user.setUsername(signupDto.getUsername());
            user.setName(signupDto.getName());
            user.setSurname(signupDto.getSurname());
            user.setChannelId(signupDto.getChannelId());
            userRepository.save(user);

            emailDtoWithAttachment.setIsHtml(Boolean.TRUE);
            emailDtoWithAttachment.setRecipient(signupDto.getEmail());
            emailDtoWithAttachment.setUserName(signupDto.getUsername());
            emailDtoWithAttachment.setPassword(signupDto.getPassword().trim());
            if (signupDto.getChannelId() == 1) {
                emailDtoWithAttachment.setSubject("EcoQRCode İle Yeşil Dünyaya Hoşgeldiniz");
                emailDtoWithAttachment.setMsgBody("EcoQRCode ile daha yeşil bir dünyaya ilk adımı atmış bulunuyorsunuz." +
                        "Kullanıcı adı ve şifreniz ile sisteme giriş sağlayarak görünmesini istediğiniz tüm bilgilerin yanı sıra" +
                        "hakkınızda bilgiler vererek dijital kartvizitinizi oluşturabilirsiniz.");
            } else {
                emailDtoWithAttachment.setSubject("Pruvatag Dünyasına Hoşgeldiniz");
                emailDtoWithAttachment.setMsgBody("Pruvatag ile daha yeşil bir dünyaya ilk adımı atmış bulunuyorsunuz." +
                        "Kullanıcı adı ve şifreniz ile sisteme giriş sağlayarak görünmesini istediğiniz tüm bilgilerin yanı sıra" +
                        "hakkınızda bilgiler vererek dijital kartvizitinizi oluşturabilirsiniz.");
            }

            communicationClient.sendMailWithAttachment(emailDtoWithAttachment);

            signupResponse.setStatus(1);
            signupResponse.setMessage("User signup is done.");
            return ResponseEntity.ok(signupResponse);
        } catch (Exception e) {
            signupResponse.setStatus(0);
            signupResponse.setErrorMessage(e.getMessage());
            signupResponse.setErrorCode(-99);
            return ResponseEntity.ok(signupResponse);
        }
    }

    public ResponseEntity<String> changePasswordService(ChangePasswordDto changePasswordDto) {
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            Optional<User> optionalUser = userRepository.findByUsernameOrEmail(changePasswordDto.getUserName(),changePasswordDto.getUserName());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
                    user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
                    userRepository.save(user);
                } else {
                    return ResponseEntity.badRequest().body("Password is not same.");
                }
                return ResponseEntity.ok("Process is done.");
            } else {
                return ResponseEntity.ok("User Not Found.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ResponseEntity<Boolean> validateToken(String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(jwtTokenUtil.getUsernameFromToken(jwtToken));
            jwtTokenUtil.validateToken(jwtToken, userDetails);
            return ResponseEntity.ok(Boolean.TRUE);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Boolean.FALSE);
        }
    }

    public List<User> getAllUser(Integer channelId) {
        return userRepository.findAllByChannelIdOrderByIdDesc(channelId);
    }

    public ResponseEntity<String> forgotPassword(String email) {
        try {
            EmailDtoWithAttachment emailDtoWithAttachment = new EmailDtoWithAttachment();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setPassword(passwordEncoder.encode(user.getUsername()+"123"));
                userRepository.save(user);

                emailDtoWithAttachment.setIsHtml(Boolean.TRUE);
                emailDtoWithAttachment.setRecipient(user.getEmail());
                emailDtoWithAttachment.setUserName(user.getUsername());
                emailDtoWithAttachment.setPassword(user.getUsername()+"123");
                emailDtoWithAttachment.setSubject("Parola Sıfırlama");
                emailDtoWithAttachment.setMsgBody("Parolanız sıfırlanmıştır mailde gelen bilgi ile sisteme giriş yapabilirsiniz.");

                communicationClient.sendMailWithAttachment(emailDtoWithAttachment);

                return ResponseEntity.ok("Password created successfully.");
            } else {
                return ResponseEntity.ok("User Not Found.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
