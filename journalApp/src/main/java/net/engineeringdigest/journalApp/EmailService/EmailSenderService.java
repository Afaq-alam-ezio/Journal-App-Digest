package net.engineeringdigest.journalApp.EmailService;

import net.engineeringdigest.journalApp.Config.EmailConfig;
import net.engineeringdigest.journalApp.Config.SentimentEnumConfig;
import net.engineeringdigest.journalApp.Entities.Users;
import net.engineeringdigest.journalApp.Repositories.UsersRepo;
import net.engineeringdigest.journalApp.Services.SentimentAnalysisService;
import net.engineeringdigest.journalApp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserService userService;

    @Autowired
    private SentimentAnalysisService SAService;

    public String sendMail(EmailConfig emailData){

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(emailData.getSendTo());
        mailMessage.setSubject(emailData.getSubject());
        mailMessage.setText(emailData.getBody());

        javaMailSender.send(mailMessage);

        return "Mail sent successfully";
    }

//    Below runs every 20 seconds to send a message and also auto managed by spring boot scheduler
    @Scheduled(cron = "*/12 * * * * *")
    public void sendMailViaScheduler(){

        try
        {
            List<Users> userDetails = userService.getByEmailAndSA();

            for (Users user : userDetails) {

//                List<SentimentEnumConfig> filteredUserJournalEntries = user.getJournalEntries().stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x -> x.getContent()).collect(Collectors.toList());
//                String finalContent = String.join(" ", filteredUserJournalEntries);
//                int SAData = SAService.getSA(finalContent);

                List<SentimentEnumConfig> filteredSentiments = user.getJournalEntries().stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x -> x.getSentiment()).collect(Collectors.toList());

                Map<SentimentEnumConfig, Integer> sentimentMap = new HashMap<>();

                for (SentimentEnumConfig sentiment : filteredSentiments) {

                    sentimentMap.put(sentiment, sentimentMap.getOrDefault(sentiment, 0) + 1);
                }

                SentimentEnumConfig maxSentimentKey = null;
                int maxSentimentValue = 0;
                for (Map.Entry<SentimentEnumConfig, Integer> data : sentimentMap.entrySet()) {

                    if (data.getValue() > maxSentimentValue) {

                        maxSentimentValue = data.getValue();
                        maxSentimentKey = data.getKey();
                    }
                }

                EmailConfig emailData = new EmailConfig();
                emailData.setSendTo(user.getEmail());
                emailData.setSubject("Report related to weekly sentimental analysis");
                emailData.setBody("Overall sentimental analysis is : " + maxSentimentKey.toString() + " with upto " + maxSentimentValue + " times.");

                sendMail(emailData);


//                EmailConfig emailData = new EmailConfig();
//                emailData.setSendTo(user.getEmail());
//                emailData.setSubject("Report related to weekly sentimental analysis");
////          below setting body
//                switch (SAData) {
//                    case 1 -> emailData.setBody("your sentimental analysis is : positive");
//                    case -1 -> emailData.setBody("your sentimental analysis is : negative");
//                    default -> emailData.setBody("your sentimental analysis is : neutral");
//                }
//
//                sendMail(emailData);

            }

            System.out.println("mail sent successfully");
        }
        catch (Exception e) {

            System.out.println("Error occurred" + e);
        }
    }
}
























