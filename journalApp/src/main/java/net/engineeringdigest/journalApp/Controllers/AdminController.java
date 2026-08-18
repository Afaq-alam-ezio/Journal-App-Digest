package net.engineeringdigest.journalApp.Controllers;


import net.engineeringdigest.journalApp.CacheConfigs.WeatherCache;
import net.engineeringdigest.journalApp.Entities.Journal;
import net.engineeringdigest.journalApp.Entities.Users;
import net.engineeringdigest.journalApp.Services.JournalService;
import net.engineeringdigest.journalApp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private JournalService journalService;

    @Autowired
    private UserService userService;

    @Autowired
    private WeatherCache weatherCache;

    @GetMapping("/getAllJournals")
    public ResponseEntity<List<Journal>> getJournal(){

        return new ResponseEntity<>(journalService.getAllData(), HttpStatus.ACCEPTED);
    }

    @GetMapping("/refreshCache")
    public ResponseEntity<String> refreshCache(){

        try{

            weatherCache.init();
            return new ResponseEntity<>("Cache refreshed!", HttpStatus.ACCEPTED);
        }
        catch (Exception e){

            return new ResponseEntity<>("Failed to refresh cache!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteAllJournals")
    public ResponseEntity<String> deleteData(){

        return new ResponseEntity<>(journalService.deleteAllData(), HttpStatus.ACCEPTED);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<Users>> getAllUsers(){

        return new ResponseEntity<>(userService.fetchAllUsers(), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/clearUsers")
    public ResponseEntity<?> deleteAllUsers(){

        return new ResponseEntity<>(userService.clearAllUsers(), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/deleteByUsername/{username}")
    public ResponseEntity<?> DeleteUserByUsername(@PathVariable String username){

        return new ResponseEntity<>(userService.deleteUserByUsername(username), HttpStatus.ACCEPTED);
    }
}
