package net.engineeringdigest.journalApp.Repositories;

import net.engineeringdigest.journalApp.Entities.Users;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QueryCriteriaUsingMongoTemplate {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Users> getUserByName(){

        Query query = new Query();

        query.addCriteria(Criteria.where("userName").is("ez"));

        return mongoTemplate.find(query, Users.class);
    }

    public List<Users> getUsersWithEmailAndSA(){

        Query query = new Query();

        query.addCriteria(Criteria.
                where("email").ne(null)
                .and("sentimentAnalysis").is(true));

        return mongoTemplate.find(query, Users.class);
    }
}
