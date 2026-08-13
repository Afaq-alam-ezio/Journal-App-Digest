package net.engineeringdigest.journalApp.ServiceTest;

import net.engineeringdigest.journalApp.Entities.Users;
import net.engineeringdigest.journalApp.Repositories.UsersRepo;
import net.engineeringdigest.journalApp.Services.UserService;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;


import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
//@Profile("dev or prod or any other")                                   this will not work but below will work annotation!!!
//@ActiveProfiles("dev or prod or any other")
public class JournalServiceTest {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private UserService userService;

    @Test
    public void sampleTesting(){

        assertEquals("3", "3", "Custom message if failed");
    }

    @Test
    public void getTimeOut(){

        assertTimeout(Duration.ofMillis(1), () -> usersRepo.existsByUserName("ez"));
    }

    @Test
    public void getById(){

        assertNotNull(usersRepo.findById("6a78927289e6574f9f22d10c"));
    }

    @Test
    public void getUsersByName(){

        assertTrue(usersRepo.existsByUserName("ez"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ezio", "Altair", "Connor", "ez"})
//    Your test method can only accept exactly one parameter. There's no way to pass a second, related value alongside it (like a matching password).
    public void getByUsernameUsingValueSourceParams(String name){

        assertNotNull(usersRepo.findByUserName(name));
    }

    @ParameterizedTest
    @CsvSource({"1, 2, 3", "1, 2, 3"})
    public void getByUsernameUsingCsvSourceParams(int p1, int p2, int target){

        assertEquals(target, p1 + p2);
    }

    @ParameterizedTest
    @ArgumentsSource(ArgumentsProviderClass.class)
    public void testSearchByUsername(String username, String password){

        Users user = new Users();
        user.setUserName(username);
        user.setPassword(password);

        String result = userService.addNewUser(user);

        assertEquals("User already exists", result);
    }
}

/*

JUnit 5 — Assertions Cheat Sheet


Import: org.junit.jupiter.api.Assertions.* (or static import individual methods) Comes bundled with spring-boot-starter-test -- no extra dependency needed. Test files live under: src/test/java/... (mirrors your main package structure)

        1. Equality checks
java
assertEquals(expected, actual);
assertEquals(expected, actual, "custom failure message");
assertNotEquals(unexpected, actual);

Use for: comparing return values, field values, computed results.

2. Boolean checks
java
assertTrue(condition);
assertFalse(condition);

Use for: flags, boolean-returning methods (e.g. existsByUserName()).

        3. Null checks
java
assertNull(object);
assertNotNull(object);

Use for: confirming something was/wasn't found, confirming an object got created.

        4. Reference/identity checks (rarely needed, know it exists)
java
assertSame(expected, actual);       // are they the EXACT SAME object (==)?
assertNotSame(unexpected, actual);

Different from assertEquals -- this checks identity, not just "equal value."

        5. Array/collection checks
java
assertArrayEquals(expectedArray, actualArray);
assertIterableEquals(expectedIterable, actualIterable);

Use for: comparing lists/arrays of results (e.g. getAllUsers()).

        6. Exception checks -- IMPORTANT, use often
java
// confirm a specific exception IS thrown
Exception ex = assertThrows(CustomException.class, () -> {
    journalService.getDataById(fakeId);
});
assertEquals("Journal not found", ex.getMessage());

// confirm NOTHING is thrown (should run cleanly)
assertDoesNotThrow(() -> {
        userService.addNewUser(validUser);
});

Use for: testing your custom exceptions (JournalNotFoundException, CustomException etc.) -- exactly the kind of thing built earlier in this project.

7. Timeout checks (rarely needed for CRUD apps, good to know)
java
assertTimeout(Duration.ofSeconds(1), () -> { /* must finish in time  });
assertTimeoutPreemptively(Duration.ofSeconds(1), () -> { /* aborts if too slow  });
        8. Grouped assertions -- checks ALL, reports ALL failures together
        java
assertAll(
    () -> assertEquals("Ezio", user.getUserName()),
        () -> assertTrue(user.isEnabled()),
        () -> assertNotNull(user.getId())
        );

KEY DIFFERENCE vs writing separate assertEquals lines:

Normal: first failed assertion STOPS the test, later ones never run.
        assertAll: runs every assertion regardless, reports ALL failures at once. Use whenever checking multiple fields on ONE object.
        9. Force-fail / placeholder
        java
fail("Not implemented yet");

Use for: marking a test you haven't finished writing yet, so it's visibly red instead of silently passing/absent.

Realistic examples using THIS project's classes
java
@Test
void testGetDataById_whenNotFound_throwsException() {
    ObjectId fakeId = new ObjectId();

    CustomException ex = assertThrows(CustomException.class, () -> {
        journalService.getDataById(fakeId);
    });

    assertEquals("Journal not found", ex.getMessage());
}

@Test
void testAddNewUser_successfullyCreatesUser() {
    Users newUser = new Users();
    newUser.setUserName("TestUser");
    newUser.setPassword("password123");

    String result = userService.addNewUser(newUser);

    assertEquals("User created", result);
    assertTrue(userRepo.existsByUserName("TestUser"));
}

@Test
void testUserFields_allCorrectTogether() {
    Users user = userRepo.findByUserName("TestUser");

    assertAll(
            () -> assertNotNull(user),
            () -> assertEquals("TestUser", user.getUserName()),
            () -> assertTrue(user.isEnabled())
    );
}
Quick decision guide -- which assert to reach for
What you're checking	Use this
Two values equal?	assertEquals
A condition is true/false?	assertTrue / assertFalse
Something is/isn't null?	assertNull / assertNotNull
A method throws a specific exception?	assertThrows
A method should run WITHOUT throwing?	assertDoesNotThrow
Multiple checks on one object, want all results?	assertAll
List/array contents match?	assertArrayEquals / assertIterableEquals


*/
