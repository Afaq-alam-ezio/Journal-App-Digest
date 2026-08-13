package net.engineeringdigest.journalApp.ServiceTest;

import net.engineeringdigest.journalApp.Entities.Users;
import net.engineeringdigest.journalApp.Repositories.UsersRepo;
import net.engineeringdigest.journalApp.Services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTestUsingMockito {

    @Mock
    UsersRepo usersRepo;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

//  WHEN USER ALREADY EXISTS
    @Test
    void testAddNewUserWhenUserAlreadyExists(){

        Users user = new Users();
        user.setUserName("Afaq");
        user.setPassword("Plain password");

        when(usersRepo.existsByUserName("Afaq")).thenReturn(true);
//
//        Below is never called as user already exists
//        when(passwordEncoder.encode("Plain password")).thenReturn("hashed password");

        String result = userService.addNewUser(user);

        Assertions.assertEquals("User already exists", result);
        verify(usersRepo, never()).save(any());
    }

    @Test
    void testAddNewUserWhenUserDoesntExists(){

        Users user = new Users();
        user.setUserName("EZ");
        user.setPassword("plain password");

        when(usersRepo.existsByUserName("EZ")).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashed password");

        String result = userService.addNewUser(user);

        Assertions.assertEquals("User created", result);
        verify(usersRepo).save(any());
    }
}
