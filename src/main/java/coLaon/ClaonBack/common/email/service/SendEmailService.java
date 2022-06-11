package coLaon.ClaonBack.common.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import com.sun.mail.smtp.SMTPAddressFailedException;
import org.springframework.core.NestedExceptionUtils;

import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.MessagingException;
import org.springframework.web.util.HtmlUtils;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.UnavailableMailServerException;
import coLaon.ClaonBack.common.exception.InternalServerErrorException;
import coLaon.ClaonBack.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class SendEmailService {
    private final JavaMailSender emailSender;
    
    private static final String FROM_ADDRESS = "CLAON <help.claon@gmail.com>";
    private static final String HEADER_LOGO_IMG_URL = "";
    private static final String HEADER_TITLE = "CLAON";
    private static final String WRAPPER_BGCOLOR_RGB = "15,31,127"; // "r,g,b" (0-255 for each r/g/b)


    public void sendEmail(String subject, String body, String toAddress) {
        sendEmail(subject, body, false, new String[] { toAddress }, null, null);
    }
    
    public void sendEmail(String subject, String body, String[] toAddresses) {
        sendEmail(subject, body, false, toAddresses, null, null);
    }

    public void sendEmail(String subject, String body, boolean htmlBody, String toAddress) {
        sendEmail(subject, body, htmlBody, new String[] { toAddress }, null, null);
    }
    
    public void sendEmail(String subject, String body, boolean htmlBody, String[] toAddresses) {
        sendEmail(subject, body, htmlBody, toAddresses, null, null);
    }

    public void sendEmail(String subject, String body,
                          String[] toAddresses, String[] ccAddresses, String[] bccAddresses) {
        sendEmail(subject, body, false, toAddresses, ccAddresses, bccAddresses);
    }

    
    public void sendEmail(String subject, String body, boolean htmlBody,
                          String[] toAddresses, String[] ccAddresses, String[] bccAddresses) {
        
        MimeMessage msg = emailSender.createMimeMessage();
        String wrappedBody = wrapBodyInEmailTemplate(body, htmlBody);

        
        try {
            
            MimeMessageHelper msgHelper = new MimeMessageHelper(msg, true, "UTF-8");
            
            msgHelper.setFrom(FROM_ADDRESS);
            msgHelper.setTo(toAddresses);
            msgHelper.setSubject(subject);
            msgHelper.setText(wrappedBody, true);
            if (ccAddresses != null) {
                msgHelper.setCc(ccAddresses);
            }
            if (bccAddresses != null) {
                msgHelper.setBcc(bccAddresses);
            }
            
        } catch (MessagingException e) {
            throw new BadRequestException(ErrorCode.INVALID_FORMAT, "메일 구성 중 오류가 발생했습니다.");
        }


        try {
            
            emailSender.send(msg);

        } catch (MailSendException e) {

            // invalid address format
            for (Exception innerE : e.getMessageExceptions()) {
                if (NestedExceptionUtils.getMostSpecificCause(innerE) instanceof SMTPAddressFailedException) {
                    throw new BadRequestException(ErrorCode.INVALID_FORMAT, "잘못된 메일 주소 형식입니다.");
                }
            }

            // others (timeout, etc.)
            throw new UnavailableMailServerException(ErrorCode.UNAVAILABLE_MAIL_SERVER, "메일 전송 중 오류가 발생했습니다.");
            
        } catch (MailException e) {
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR, "메일 처리 서버 오류입니다." );
        }
    }

    
    private static String wrapBodyInEmailTemplate(String body, boolean htmlBody) {
        
        final String DOCTYPE =
            ("<!DOCTYPE " +
             "htmlPUBLIC " +
             "'-//W3C//DTD XHTML 1.0 Transitional//EN' " +
             "'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>");
      
        final String EMAIL_HEADER =
            ("<table border='0' cellpadding='0' cellspacing='0'>"
             + ("<tr>"
                + ("<td>"
                   + ("<img src='" + HEADER_LOGO_IMG_URL + "' width='60' height='60' alt='( Logo )'>")
                   + "</td>")
                + ("<td style='width: 20px'>"
                   + "</td>")
                + ("<td>"
                   + ("<h1 style='margin: 0'>"
                      + HEADER_TITLE
                      + "</h1>")
                   + "</td>")
                + "</tr>")
             + "</table>");
        
        final String EMAIL_FOOTER =
            ("<p style='margin: 0'>"
             + "본 메일은 발신 전용으로 회신되지 않습니다."
             + "<br>"
             + "Copyright © CLAON. All Rights Reserved."
             + "</p>");

        final String HORIZONTAL_DIVIDER =
            ("<tr style='height: 2px; padding: 0; background: rgb(" + WRAPPER_BGCOLOR_RGB + ")'>"
             + ("<td>"
                + "</td>")
             + "</tr>");
        
        
        return
            DOCTYPE +
            ("<html xmlns='http://www.w3.org/1999/xhtml'>"
             + ("<body>"
                + ("<table style='margin: auto; padding: 20px; width: 600px; border: 0'>"
                   + ("<tbody>"


                      // header row
                      + ("<tr>"
                         + ("<td style='" + ("padding: 30px; " +
                                             "background: rgba(" + WRAPPER_BGCOLOR_RGB + ", 0.2)'>")
                            + EMAIL_HEADER
                            + "</td>")
                         + "</tr>")
                      
                            
                      + HORIZONTAL_DIVIDER


                      // body row
                      + ("<tr>"
                         + ("<td style='padding: 50px'>"
                            // wrap in <pre> and escape if text mode (non-html body)
                            + (htmlBody
                               ? body
                               : ("<pre>" + HtmlUtils.htmlEscape(body) + "</pre>") )
                            + "</td>")
                         + "</tr>")

                            
                      + HORIZONTAL_DIVIDER


                      // footer row
                      + ("<tr>"
                         + ("<td style='" + ("padding: 30px; " +
                                             "background: rgba(" + WRAPPER_BGCOLOR_RGB + ", 0.2); " +
                                             "font-size: 10px") + "'>"
                            + EMAIL_FOOTER
                            + "</td>")
                         + "</tr>")

                      
                      + "</tbody>")
                   + "</table>")
                + "</body>")
             + "</html>");
    }
}
