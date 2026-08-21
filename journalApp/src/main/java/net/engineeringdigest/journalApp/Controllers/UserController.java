package net.engineeringdigest.journalApp.Controllers;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.Entities.Journal;
import net.engineeringdigest.journalApp.Entities.Users;
import net.engineeringdigest.journalApp.SecurityConfig.UserDetailsServiceImpl;
import net.engineeringdigest.journalApp.Services.JournalService;
import net.engineeringdigest.journalApp.Services.UserService;
import net.engineeringdigest.journalApp.Utils.JWTAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api")   // Base path for ALL methods in this class -> every
// mapping below gets "/api" prefixed automatically
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JournalService journalService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JWTAuth jwtAuth;

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users userData){

//        never use below as the request currently is not authenticated yet, is still at the securityFilterChain
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();

        try{

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userData.getUsername(), userData.getPassword()));

//         userDetailsService.loadUserByUsername(userData.getUsername()); ->>>  authenticationManager.authenticate(...) already calls loadUserByUsername
//                                                                              internally as part of its verification process, calling it again is redundant
            String jwtToken =  jwtAuth.generateToken(userData.getUsername());


            log.info("Token created!");
            return new ResponseEntity<>(jwtToken, HttpStatus.FOUND);

        } catch (Exception e) {

            log.error("Error occurred : " + e);
            return new ResponseEntity<>("Error occurred : " + e, HttpStatus.NOT_FOUND);
        }
    }

//        if(newUserData.getUsername() != null && !newUserData.getUsername().isEmpty() && !newUserData.getUsername().isBlank()){
//        if(StringUtils.hasText(newUserData.getUsername()) && StringUtils.hasText(newUserData.getPassword())){
//
////            log.error("new user with name = {} and password = {} is created.", newUserData.getUsername(), newUserData.getPassword());
//            return new ResponseEntity<>(userService.addNewUser(newUserData), HttpStatus.CREATED);
//        }
//
////        logger.error("credentials not passed properly");  simply use the instance name " log " of SLF4j
////        log.error("credentials not passed properly");
//
////        return new ResponseEntity<>("Invalid credentials", HttpStatus.NOT_ACCEPTABLE);
//    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Users userData){

        try{
//
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(userData.getUsername(), userData.getPassword()));

//         userDetailsService.loadUserByUsername(userData.getUsername()); ->>>  authenticationManager.authenticate(...) already calls loadUserByUsername
//                                                                              internally as part of its verification process, calling it again is redundant

            if (userService.addNewUser(userData).equals("User already exists")){

                return new ResponseEntity<>("User already exists! kindly login using creds!", HttpStatus.NOT_ACCEPTABLE);
            }
            String jwtToken =  jwtAuth.generateToken(userData.getUsername());


            log.info("Token created!");
            return new ResponseEntity<>(jwtToken, HttpStatus.CREATED);

        } catch (Exception e) {

            log.error("Error occurred : " + e);
            return new ResponseEntity<>("Error occurred : " + e, HttpStatus.NOT_FOUND);
        }

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
