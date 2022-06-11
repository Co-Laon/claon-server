package coLaon.ClaonBack.common.email.controller;

import java.util.List;

import coLaon.ClaonBack.common.email.dto.SendEmailRequestDto;
import coLaon.ClaonBack.common.email.service.SendEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
public class SendEmailController {
    private final SendEmailService sendEmailService;

    @PostMapping("/send")
    public void sendEmail(@RequestBody @Valid SendEmailRequestDto sendEmailRequestDto) {
        List<String> cc = sendEmailRequestDto.getCcAddresses();
        List<String> bcc = sendEmailRequestDto.getBccAddresses();
        
        sendEmailService.sendEmail(sendEmailRequestDto.getSubject(),
                                   sendEmailRequestDto.getBody(),
                                   sendEmailRequestDto.isHtmlBody(),
                                   sendEmailRequestDto.getToAddresses().toArray(new String[0]),
                                   (cc != null ? cc.toArray(new String[0]) : null),
                                   (bcc != null ? bcc.toArray(new String[0]) : null));
    }
}
