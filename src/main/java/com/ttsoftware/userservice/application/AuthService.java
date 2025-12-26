package com.ttsoftware.userservice.application;

import com.ttsoftware.userservice.application.config.JwtTokenUtil;
import com.ttsoftware.userservice.client.CommunicationClient;
import com.ttsoftware.userservice.domain.dto.*;
import com.ttsoftware.userservice.domain.entity.Role;
import com.ttsoftware.userservice.domain.entity.User;
import com.ttsoftware.userservice.infrastructure.RoleRepository;
import com.ttsoftware.userservice.infrastructure.UserRepository;
import com.ttsoftware.userservice.model.JwtResponse;
import com.ttsoftware.userservice.model.SignupResponse;
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

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CommunicationClient communicationClient;
    private final VerificationService verificationService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    public ResponseEntity<JwtResponse> loginService(LoginDto loginDto) {
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + loginDto.getEmail()));

            if (!user.isEnabled()) {
                return ResponseEntity.badRequest().body(new JwtResponse("Email not verified."));
            }

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            final UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginDto.getEmail());
            final String token = jwtTokenUtil.generateToken(userDetails, user.getEmail(), user.getRoles());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new JwtResponse(e.getMessage()));
        }
    }


    public ResponseEntity<SignupResponse> signupUserService(SignupDto signupDto) {
        User user = new User();
        Set<Role> role = Collections.singleton(roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("Role not found")));
        EmailDtoWithAttachment emailDtoWithAttachment = new EmailDtoWithAttachment();
        SignupResponse signupResponse = new SignupResponse();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        try {
            String encodedPassword = passwordEncoder.encode(signupDto.getPassword().trim());
            user.setEmail(signupDto.getEmail());
            user.setPassword(encodedPassword);
            user.setName(signupDto.getName());
            user.setSurname(signupDto.getSurname());
            user.setPhone(signupDto.getPhone());
            user.setPhoneNumber(signupDto.getPhoneNumber());
            user.setCountry(signupDto.getCountry());
            user.setCity(signupDto.getCity());
            user.setAddress(signupDto.getAddress());
            user.setDealerName(signupDto.getDealerName());
            user.setTaxNumber(signupDto.getTaxNumber());
            user.setTaxOffice(signupDto.getTaxOffice());
            user.setDealer(signupDto.isDealer());
            user.setRoles(role);
            user.setEnabled(false);
            userRepository.save(user);

            verificationService.sendVerificationEmail(user);

            signupResponse.setStatus(1);
            signupResponse.setMessage("User signup is done. Please verify your email before login.");
            return ResponseEntity.ok(signupResponse);


//            emailDtoWithAttachment.setIsHtml(Boolean.TRUE);
//            emailDtoWithAttachment.setRecipient(signupDto.getEmail());
//            emailDtoWithAttachment.setUserName(signupDto.getDealerName());
//            emailDtoWithAttachment.setPassword(signupDto.getPassword().trim());
//            emailDtoWithAttachment.setSubject("Eren Tarım Sistemine Hoşgeldiniz");
//            emailDtoWithAttachment.setMsgBody("Merhaba " + signupDto.getDealerName() + ",\n" +
//                    "\n" +
//                    "Eren Tarım Ürünleri ailesine hoş geldiniz!\n" +
//                    "\n" +
//                    "Sisteme başarıyla kaydoldunuz. Aşağıda giriş bilgileriniz yer almaktadır:\n" +
//                    "Kullanıcı Adı: "+signupDto.getEmail()+"\n" +
//                    "Şifre: "+signupDto.getPassword()+"\n" +
//                    "\n" +
//                    "Sisteme giriş yaptıktan sonra profilinizi tamamlayabilir, ürünlerimizi inceleyebilir ve avantajlarımızdan yararlanmaya başlayabilirsiniz.\n" +
//                    "\n" +
//                    "Herhangi bir sorunuz olursa bizimle iletişime geçmekten çekinmeyin.\n" +
//                    "\n" +
//                    "Yeşil ve verimli bir gelecek için birlikteyiz!\n" +
//                    "\n" +
//                    "Saygılarımızla,  \n" +
//                    "Eren Tarım Ürünleri Ekibi  \n" +
//                    "https://erentarimurunleri.com/");
//
//            communicationClient.sendMailWithAttachment(emailDtoWithAttachment);
//            signupResponse.setMessage("User signup is done.");
//            return ResponseEntity.ok(signupResponse);
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
            Optional<User> optionalUser = userRepository.findByUsernameOrEmail(changePasswordDto.getUserName(), changePasswordDto.getUserName());
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

    public ResponseEntity<String> forgotPassword(String email) {
        try {
            EmailDtoWithAttachment emailDtoWithAttachment = new EmailDtoWithAttachment();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setPassword(passwordEncoder.encode(user.getUsername() + "123"));
                userRepository.save(user);

                emailDtoWithAttachment.setIsHtml(Boolean.TRUE);
                emailDtoWithAttachment.setRecipient(user.getEmail());
                emailDtoWithAttachment.setUserName(user.getUsername());
                emailDtoWithAttachment.setPassword(user.getUsername() + "123");
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

    public ResponseEntity<Long> getUserIDByEmail(EmailQueryRequest email) {
        try {
            Optional<User> user = userRepository.findByEmail(email.getEmail());
            return user.map(value -> ResponseEntity.ok(value.getId())).orElseGet(() -> ResponseEntity.badRequest().body(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    public ResponseEntity<List<UserDto>> getAllUser() {
        try {
            List<User> users = userRepository.findAll();
            List<UserDto> userDtoList = users.stream().map(x -> {
                UserDto userDto = new UserDto();
                userDto.setId(x.getId());
                userDto.setName(x.getName());
                userDto.setSurname(x.getSurname());
                userDto.setEmail(x.getEmail());
                userDto.setUsername(x.getUsername());
                userDto.setCreatedDate(x.getCreatedDate());
                return userDto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(userDtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}