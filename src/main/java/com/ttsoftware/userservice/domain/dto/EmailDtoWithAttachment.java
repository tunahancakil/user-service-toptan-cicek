package com.ttsoftware.userservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDtoWithAttachment {
    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;
    private String qrCodeImage;
    private String userName;
    private String password;
    private Boolean isHtml;
}
