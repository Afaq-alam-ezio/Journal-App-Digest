package net.engineeringdigest.journalApp.RepoTest;

import net.engineeringdigest.journalApp.Repositories.QueryCriteriaUsingMongoTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class QueryCriteriaUsingMongoTemplateTest {

    @Autowired
    private QueryCriteriaUsingMongoTemplate test;

    @Test
    public void findByName(){

        Assertions.assertNotNull(test.getUserByName());
    }

    @Test
    public void getUsersWithEmailAndSATest(){

        Assertions.assertNotNull(test.getUsersWithEmailAndSA());
    }
}
