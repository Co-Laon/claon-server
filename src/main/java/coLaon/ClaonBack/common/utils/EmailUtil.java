package coLaon.ClaonBack.common.utils;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.InternalServerErrorException;
import coLaon.ClaonBack.common.exception.ServiceUnavailableException;
import coLaon.ClaonBack.config.EmailSenderConfig;
import com.sun.mail.smtp.SMTPAddressFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class  EmailUtil {
    private final EmailSenderConfig emailSenderConfig;

    private static JavaMailSender emailSender;
    private static String fromAddress;

    @PostConstruct
    private void initialize() {
        emailSender = emailSenderConfig.getEmailSender().orElseGet(() -> {
            log.warn("Emailsender properties do not exist!");
            return null;
        });
        fromAddress = emailSenderConfig.getFromAddress();
    }

    public static void send(String subject, String body,
                            String toAddress) {
        send(subject, body, false, new String[]{toAddress}, null, null);
    }

    public static void send(String subject, String body,
                            String[] toAddresses) {
        send(subject, body, false, toAddresses, null, null);
    }

    public static void send(String subject, String body, boolean isBodyHtml,
                            String toAddress) {
        send(subject, body, isBodyHtml, new String[]{toAddress}, null, null);
    }

    public static void send(String subject, String body, boolean isBodyHtml,
                            String[] toAddresses) {
        send(subject, body, isBodyHtml, toAddresses, null, null);
    }

    public static void send(String subject, String body,
                            String[] toAddresses, String[] ccAddresses, String[] bccAddresses) {
        send(subject, body, false, toAddresses, ccAddresses, bccAddresses);
    }

    public static void send(String subject, String body, boolean isBodyHtml,
                            String[] toAddresses, String[] ccAddresses, String[] bccAddresses) {
        if (emailSender == null) {
            throw new InternalServerErrorException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "메일 설정 초기화 오류입니다."
            );
        }

        MimeMessage msg = emailSender.createMimeMessage();
        String wrappedBody = wrapBodyInEmailTemplate(body, isBodyHtml);

        try {
            MimeMessageHelper msgHelper = new MimeMessageHelper(msg, true, "UTF-8");

            if (fromAddress != null) {
                msgHelper.setFrom(fromAddress);
            }
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
            e.printStackTrace();
            throw new BadRequestException(
                    ErrorCode.INVALID_FORMAT,
                    "메일 구성 중 오류가 발생했습니다."
            );
        }

        try {
            emailSender.send(msg);
        } catch (MailSendException e) {
            e.printStackTrace();

            // invalid address format
            for (Exception innerE : e.getMessageExceptions()) {
                if (NestedExceptionUtils.getMostSpecificCause(innerE) instanceof SMTPAddressFailedException) {
                    throw new BadRequestException(
                            ErrorCode.INVALID_FORMAT,
                            "잘못된 메일 주소 형식입니다."
                    );
                }
            }

            // others (timeout, etc.)
            throw new ServiceUnavailableException(
                    ErrorCode.SERVICE_UNAVAILABLE,
                    "메일 전송 중 오류가 발생했습니다."
            );
        } catch (MailException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "메일 처리 서버 오류입니다."
            );
        }
    }

    private static String wrapBodyInEmailTemplate(String body, boolean isBodyHtml) {
        final String HEADER_LOGO_IMG_URL = "";
        final String HEADER_TITLE = "CLAON";
        final String WRAPPER_BGCOLOR_RGB = "15,31,127"; // "r,g,b" (0-255 for each r/g/b)

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

        return DOCTYPE +
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
                        + (isBodyHtml
                        ? body
                        : ("<pre>" + HtmlUtils.htmlEscape(body) + "</pre>"))
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
