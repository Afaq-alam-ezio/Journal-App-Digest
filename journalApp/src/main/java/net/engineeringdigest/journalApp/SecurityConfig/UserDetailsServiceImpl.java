package net.engineeringdigest.journalApp.SecurityConfig;

import net.engineeringdigest.journalApp.Entities.Users;
import net.engineeringdigest.journalApp.Repositories.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersRepo usersRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users userInfo = usersRepo.findByUserName(username);

        if(!Objects.equals(userInfo.getUsername(), username)){

            throw new UsernameNotFoundException("Username not found!");
        }

        return userInfo;
    }
}
