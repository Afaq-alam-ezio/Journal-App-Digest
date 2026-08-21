package net.engineeringdigest.journalApp.Services;

import net.engineeringdigest.journalApp.Config.EmailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class EmailSenderController {

    @Autowired
    private JournalService.EmailSenderService emailSenderService;

    @PostMapping("/sendMail")
    public ResponseEntity<String> mailSender(@RequestBody EmailConfig emailData){

        try{

            return new ResponseEntity<>(emailSenderService.sendMail(emailData), HttpStatus.ACCEPTED);
        }
        catch (Exception e) {

            return new ResponseEntity<>("Error : " + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
