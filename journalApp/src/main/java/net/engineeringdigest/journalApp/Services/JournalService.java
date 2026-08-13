package net.engineeringdigest.journalApp.Services;

import net.engineeringdigest.journalApp.Entities.Journal;
import net.engineeringdigest.journalApp.Entities.Users;
import net.engineeringdigest.journalApp.Repositories.JournalRepo;
import net.engineeringdigest.journalApp.Repositories.UsersRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Component
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
}
