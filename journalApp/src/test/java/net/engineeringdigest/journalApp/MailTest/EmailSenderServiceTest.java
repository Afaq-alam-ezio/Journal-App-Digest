package net.engineeringdigest.journalApp.MailTest;

import net.engineeringdigest.journalApp.Config.EmailConfig;
import net.engineeringdigest.journalApp.EmailService.EmailSenderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
public class EmailSenderServiceTest {

    @Autowired
    private EmailSenderService emailSenderService;

    @Test
    public void EmailSenderServiceTest(){

        EmailConfig emailData = new EmailConfig();
        emailData.setSendTo("afaqalam323@gmail.com");
        emailData.setSubject("Password Reset Request");
        emailData.setBody("We received a request to reset your password. If this wasn't you, please ignore this email");

        Assertions.assertEquals("Mail sent successfully", emailSenderService.sendMail(emailData));

    }
}
