package com.ttsoftware.userservice.client;

import com.ttsoftware.userservice.domain.dto.EmailDtoWithAttachment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.PostMapping;

@Import(FeignClientsConfiguration.class)
@FeignClient(value = "${feign.communication.name}", url = "${feign.communication.url}")
public interface CommunicationClient {
    @PostMapping("/email/sendMailWithAttachment")
    String sendMailWithAttachment(EmailDtoWithAttachment emailDtoWithAttachment);
}
