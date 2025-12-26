package com.ttsoftware.userservice.application;

import com.ttsoftware.userservice.application.VerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class VerificationController {
    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        boolean ok = verificationService.validateVerificationToken(token);
        if (ok) {
            return ResponseEntity.ok("E-posta doğrulandı. Giriş yapabilirsiniz.");
        } else {
            return ResponseEntity.badRequest().body("Token geçersiz veya süresi dolmuş.");
        }
    }
}
