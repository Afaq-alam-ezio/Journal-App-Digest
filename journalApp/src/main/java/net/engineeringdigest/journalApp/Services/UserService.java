package net.engineeringdigest.journalApp.Services;

import net.engineeringdigest.journalApp.CacheConfigs.WeatherCache;
import net.engineeringdigest.journalApp.Entities.Journal;
import net.engineeringdigest.journalApp.Entities.Users;
import net.engineeringdigest.journalApp.Repositories.JournalRepo;
import net.engineeringdigest.journalApp.Repositories.QueryCriteriaUsingMongoTemplate;
import net.engineeringdigest.journalApp.Repositories.UsersRepo;
import net.engineeringdigest.journalApp.POJOs.WeatherData;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    private JournalRepo journalRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private QueryCriteriaUsingMongoTemplate queryCriteria;

    @Autowired
    private WeatherCache weatherCache;

    @Value("${weather.apiKey}")
    private String APIKEY;

    @Value("${weather.url}")
    private String URL;

    public List<Users> fetchAllUsers() {

        return userRepo.findAll();
    }

    public String getUserName(String username) {

//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();

        Users data = userRepo.findByUserName(username);

        return data.getUsername();
    }

    public String addNewUser(Users newUserData) {

        boolean result = userRepo.existsByUserName(newUserData.getUsername());

        if(result){

            return "User already exists";
        }


        newUserData.setPassword(passwordEncoder.encode(newUserData.getPassword()));
        userRepo.save(newUserData);
        return "User created";
    }


    public String updateUser(Users userData) {

        boolean result = userRepo.existsByUserName(userData.getUsername());

        if(result){

            Users userWithExitingId = userRepo.findByUserName(userData.getUsername());

//            Users user = new Users();
//
//            user.setId(UserWithExitingId.getId());
//            user.setUserName(userData.getUserName());
//            user.setPassword(userData.getPassword());

//            userRepo.save(user);
            userWithExitingId.setPassword(passwordEncoder.encode(userData.getPassword() ));

            userRepo.save(userWithExitingId);
            return "Updated successfully";
        }

        return "User doesn't exists";
    }

    public List<Journal> getJournalByUserName() {

//        List<Journal> journalEntries = new ArrayList<>(userRepo.findByUserName(userName).getJournalEntries());

        // below is shortcut rather than fetching into any variable and returning that variable

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return new ArrayList<>(userRepo.findByUserName(username).getJournalEntries());
    }

    @Transactional
    public String deleteJournalForUser(ObjectId journalId) {

        if(journalRepo.existsById(journalId)){

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            journalRepo.deleteById(journalId);
            if(userRepo.existsByUserName(username)){

                // If any error happens after 50% task has been done of user saving, thus now only remains journal saving,
                // then everything will be rolled back if any errors happens here before saving due to --- @Transactional --- annotation

                userRepo.findByUserName(username).getJournalEntries().removeIf(x -> x.getId().equals(journalId));

                return "Journal entry with id = "+ journalId + " for " + username + " is deleted successfully";
            }

            return "Username doesn't exist";

        }
        else {

            return "Journal id doesn't exist";
        }
    }


    public ResponseEntity<WeatherData> getWeather(String cityName) {

        String finalURl = weatherCache.weatherCacheData
                .get("weatherApi")
                .replace("cityName", cityName)
                .replace("APIKEY", APIKEY);

        ResponseEntity<WeatherData> response = restTemplate.exchange(finalURl, HttpMethod.GET, null, WeatherData.class);

        return response;
    }

    public String clearAllUsers() {

        userRepo.deleteAll();
        return "Cleared successfully!";
    }

    public String deleteUserByUsername(String username) {

        Users user = userRepo.findByUserName(username);

        if(user != null){

            userRepo.deleteById(user.getId());
            return "user deleted successfully";
        }

        return "User Not found";
    }

    public List<Users> getByEmailAndSA() {

        return queryCriteria.getUsersWithEmailAndSA();
    }
}
