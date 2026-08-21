package net.engineeringdigest.journalApp.Services;

import net.engineeringdigest.journalApp.Config.EmailConfig;
import net.engineeringdigest.journalApp.Config.SentimentEnumConfig;
import net.engineeringdigest.journalApp.Entities.Journal;
import net.engineeringdigest.journalApp.Entities.Users;
import net.engineeringdigest.journalApp.Repositories.JournalRepo;
import net.engineeringdigest.journalApp.Repositories.UsersRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JournalService {

    @Autowired
    private JournalRepo journalRepo;

    @Autowired
    private UsersRepo usersRepo;


    public List<Journal> getAllData() {

        return journalRepo.findAll();
    }

//    public ResponseEntity<String> createNewDataByUserName(String userName, Journal newData) {
//
////        journalRepo.insert(newData);   // throws exception
//        newData.setDate(LocalDateTime.now());
//
////        journalRepo.findAll().stream().map(existing -> {
////
////            if(existing.getContent().equals(newData.getContent()) && existing.getDescription().equals(newData.getDescription())){
////
////                newData.setId(existing.getId());
////            }
////
////            Journal newlyAddedJournal = journalRepo.save(newData);     // updates simply if already available and returns saved journal data with Object id.
//
////       ---------------- NOT USING MAP HERE AS IT REQUIRES A RETURN STATEMENT  ---------------------
////        });
//
//        for(Journal existing : journalRepo.findAll()){
//
//            if(existing.getContent().equals(newData.getContent()) && existing.getDescription().equals(newData.getDescription())){
//
//                newData.setId(existing.getId());
//            }
//        }
//
//        Journal newlyAddedJournal = journalRepo.save(newData);     // updates simply if already available and returns saved journal data with Object id.
//
//        Users existingUser = usersRepo.findByUserName(userName);
//
//        if(existingUser != null){
//
//            existingUser.getJournalEntries().add(newlyAddedJournal);
//            usersRepo.save(existingUser);
//
//            return new ResponseEntity<>("Created successfully", HttpStatus.ACCEPTED);
//        }
//
//        return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);
//    }


    @Transactional
    public ResponseEntity<String> createNewDataByUserName(Journal newData) {

        // WARNING: Do NOT try to detect "duplicates" by comparing content/description
        // against existing entries (e.g. looping journalRepo.findAll() and matching
        // text, then reusing that entry's id to turn save() into an update).
        //
        // Why this is wrong:
        // 1. Two DIFFERENT journal entries can legitimately have identical text
        //    (e.g. same content written on two different days, on purpose).
        //    Matching by content would silently OVERWRITE one with the other --
        //    real data loss based on coincidence, not user intent.
        // 2. journalRepo.findAll() loads the ENTIRE collection into memory on
        //    every single create call -- gets catastrophically slow as data grows.
        // 3. It also doesn't scope by user -- would match entries across
        //    DIFFERENT users just because their text happens to be similar.
        //
        // The correct rule: "is this an update or a new entry" should NEVER be
        // guessed from content. It must come from an explicit id -- either passed
        // by the frontend (which already knows the id of whatever entry the user
        // clicked "Edit" on, from an earlier GET response), or manually via a
        // separate PUT /update/{id} endpoint. This "create" endpoint should
        // ALWAYS just insert, never guess/overwrite.

        newData.setId(null);   // force a fresh insert every time, ignore any id client might send
        newData.setDate(LocalDateTime.now());
        Journal newlyAddedJournal = journalRepo.save(newData);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Users existingUser = usersRepo.findByUserName(username);

        if(existingUser != null){
            existingUser.getJournalEntries().add(newlyAddedJournal);
            usersRepo.save(existingUser);
            return new ResponseEntity<>("Created successfully", HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);
    }


    public Journal getDataById(ObjectId id) throws Exception { // throws ChangeSetPersister.NotFoundException {

//        Journal j = new Journal();
//        j.setId("notFound");
//        j.setContent("notFound");
//        j.setDescription("notFound");
//        // no need to set date, its already null
//        return journalRepo.findById(id).orElse(j);

//        return journalRepo.findById(id).orElseThrow(() -> new ChangeSetPersister.NotFoundException());

//        return journalRepo.findById(id).orElse(null);

        try{

            return journalRepo.findById(id).orElseThrow();
        }
        catch (Exception e){

            return new Journal();
        }
    }

    public String removeData(ObjectId id) {

        if(journalRepo.existsById(id)){

            journalRepo.deleteById(id);
            return "Record deleted successfully";
        }
        else{

            return "Record not found";
        }
    }

    public String deleteAllData() {

        journalRepo.deleteAll();
        return "Data cleared";
    }

    public boolean updateDataById(ObjectId id, Journal sampleData) {

        return journalRepo.findById(id).map(existing ->
        {
            sampleData.setId(id);

            if(sampleData.getContent() == null){

                sampleData.setContent(existing.getContent());
            }
            if(sampleData.getDescription() == null){

                sampleData.setDescription(existing.getDescription());
            }

            journalRepo.save(sampleData);
            return true;
        }).orElse(false);
    }


    public String updateJournalForUser(ObjectId journalId, Journal updatedJournalData) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Users user = usersRepo.findByUserName(username);

        if (user == null) {
            return "Username doesn't exist!";
        }

        boolean ownsJournal = user.getJournalEntries()
                .stream()
                .anyMatch(entry -> entry.getId().equals(journalId));

        if (!ownsJournal) {
            return  username + " doesn't contain any journal with id = " + journalId;
        }

        Journal journal = journalRepo.findById(journalId).orElse(null);

        if (journal == null) {
            return "Journal id not found";
        }

        if (updatedJournalData.getContent() != null &&
                !updatedJournalData.getContent().isEmpty()) {
            journal.setContent(updatedJournalData.getContent());
        }

        if (updatedJournalData.getDescription() != null &&
                !updatedJournalData.getDescription().isEmpty()) {
            journal.setDescription(updatedJournalData.getDescription());
        }

        journalRepo.save(journal);

        return "Updated successfully";
    }

    @Service
    public static class EmailSenderService {

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
//        @Scheduled(cron = "*/12 * * * * *")
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
}
