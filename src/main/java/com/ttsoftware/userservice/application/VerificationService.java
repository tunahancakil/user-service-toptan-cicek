package com.ttsoftware.userservice.application;

import com.ttsoftware.userservice.client.CommunicationClient;
import com.ttsoftware.userservice.domain.dto.EmailDtoWithAttachment;
import com.ttsoftware.userservice.domain.entity.User;
import com.ttsoftware.userservice.domain.entity.VerificationToken;
import com.ttsoftware.userservice.infrastructure.UserRepository;
import com.ttsoftware.userservice.infrastructure.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationService {
    private final UserRepository userRepository;
    private final CommunicationClient communicationClient;
    private final VerificationTokenRepository tokenRepository;

    public VerificationService(VerificationTokenRepository tokenRepository,
                               UserRepository userRepository,
                               CommunicationClient communicationClient) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.communicationClient = communicationClient;
    }

    public VerificationToken createToken(User user) {
        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        return tokenRepository.save(token);
    }

    public void sendVerificationEmail(User user) {
        VerificationToken token = createToken(user);
        EmailDtoWithAttachment dto = new EmailDtoWithAttachment();
        dto.setIsHtml(Boolean.TRUE);
        dto.setRecipient(user.getEmail());
        dto.setSubject("E-posta Doğrulama");
        String verificationBaseUrl = "https://erentarimurunleri.com/api/auth/confirm?token=";
        dto.setMsgBody("Merhaba,\nLütfen e-posta adresinizi doğrulamak için aşağıdaki bağlantıya tıklayın:\n" + verificationBaseUrl + token.getToken());
        communicationClient.sendMailWithAttachment(dto);
    }

    public boolean validateVerificationToken(String token) {
        Optional<VerificationToken> opt = tokenRepository.findByToken(token);
        if (!opt.isPresent()) return false;
        VerificationToken vt = opt.get();
        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(vt);
            return false;
        }
        User user = vt.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(vt);
        return true;
    }
}
