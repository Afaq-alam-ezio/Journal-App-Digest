package net.engineeringdigest.journalApp.Controllers;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.Entities.Journal;
import net.engineeringdigest.journalApp.Entities.Users;
import net.engineeringdigest.journalApp.Services.JournalService;
import net.engineeringdigest.journalApp.Services.UserService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api")   // Base path for ALL methods in this class -> every
// mapping below gets "/api" prefixed automatically
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JournalService journalService;

//    not needed if SLF4J is used, as we have used here already, check annotation above the class name
//    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/greetings/{cityName}")
    public ResponseEntity<?> greetings(@PathVariable String cityName){

        return new ResponseEntity<>(userService.getWeather(cityName), HttpStatus.FOUND);
    }

    @GetMapping("/getEmailAndSA")
    public ResponseEntity<?> getByEmailAndSA(){

        try {

            return new ResponseEntity<>(userService.getByEmailAndSA(), HttpStatus.ACCEPTED);
        }
        catch (Exception e) {

            return new ResponseEntity<>("Error occurred : " + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getUserName/{username}")
    public ResponseEntity<?> getByUserName(@PathVariable String username){

        return new ResponseEntity<>(userService.getUserName(username),HttpStatus.ACCEPTED);
    }

    @GetMapping("/getJournalForUser")
    public ResponseEntity<?> getJournalByUserName(){

        return new ResponseEntity<>(userService.getJournalByUserName(), HttpStatus.ACCEPTED);
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> register(@RequestBody Users newUserData){

//        if(newUserData.getUsername() != null && !newUserData.getUsername().isEmpty() && !newUserData.getUsername().isBlank()){
        if(StringUtils.hasText(newUserData.getUsername()) && StringUtils.hasText(newUserData.getPassword())){

//            log.error("new user with name = {} and password = {} is created.", newUserData.getUsername(), newUserData.getPassword());
            return new ResponseEntity<>(userService.addNewUser(newUserData), HttpStatus.CREATED);
        }

//        logger.error("credentials not passed properly");  simply use the instance name " log " of SLF4j
//        log.error("credentials not passed properly");

        return new ResponseEntity<>("Invalid credentials", HttpStatus.NOT_ACCEPTABLE);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody Users userData){

        return new ResponseEntity<>(userService.updateUser(userData), HttpStatus.ACCEPTED);
    }

    @PostMapping("/addJournal")
    public ResponseEntity<?> addJournalEntryForUser(@RequestBody Journal newJournalData){

        return new ResponseEntity<>(journalService.createNewDataByUserName(newJournalData), HttpStatus.ACCEPTED);
    }
}
