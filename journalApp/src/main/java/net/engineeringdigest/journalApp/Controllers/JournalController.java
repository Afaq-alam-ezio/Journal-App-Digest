package net.engineeringdigest.journalApp.Controllers;

import net.engineeringdigest.journalApp.Entities.Journal;
import net.engineeringdigest.journalApp.Services.JournalService;
import net.engineeringdigest.journalApp.Services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api")   // Base path for ALL methods in this class -> every
// mapping below gets "/api" prefixed automatically
public class JournalController {

    @Autowired
    private JournalService journalService;

    @Autowired
    private UserService userService;



    @GetMapping("/findById/{id}")
    public ResponseEntity<Journal> getById(@PathVariable ObjectId id) throws Exception {

        return new ResponseEntity<>(journalService.getDataById(id), HttpStatus.ACCEPTED);
    }


//    @PostMapping("/")
//    public ResponseEntity<String> createEntry(@RequestBody Journal newData){
//
//        return journalService.createNewData(newData);
//    }


    @PutMapping("/addJournal/{id}")
    public ResponseEntity<String> updateDataById(@PathVariable ObjectId id, @RequestBody Journal newData){

        if(journalService.updateDataById(id, newData)){

            return new ResponseEntity<>("updated successfully", HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>("Data insufficient or id not found", HttpStatus.BAD_GATEWAY);

    }


    @PutMapping("/updateJournalForUser/{journalId}")
    public ResponseEntity<String> updateJournalForUser(
            @PathVariable ObjectId journalId,
            @RequestBody Journal updatedJournalData
    ){

        return new ResponseEntity<>(journalService.updateJournalForUser(journalId, updatedJournalData), HttpStatus.ACCEPTED);
    }

    // DELETE /api/delete/{id} -> explicit path, deletes one entry by id.
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDataById(@PathVariable ObjectId id){

        return new ResponseEntity<>(journalService.removeData(id), HttpStatus.ACCEPTED);
    }

    /**   NOTE :
     * Good to Know:
     * - @DBRef stores only references (IDs) to Journal documents.
     * - If a referenced Journal is deleted, the User may still contain a stale DBRef.
     * - When the User is loaded, missing references are not resolved.
     * - Saving the User again persists only the valid references, removing stale DBRefs.
     */
    @DeleteMapping("/deleteJournalForUser/{journalId}")
    public ResponseEntity<String> deleteJournalForUser(@PathVariable ObjectId journalId){

        return new ResponseEntity<>(userService.deleteJournalForUser(journalId), HttpStatus.ACCEPTED);
    }

}
