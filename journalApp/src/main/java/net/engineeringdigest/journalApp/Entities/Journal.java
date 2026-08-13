package net.engineeringdigest.journalApp.Entities;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Document
//@Document(collection = "journal")
public class Journal {

    @Id
    private ObjectId id;
    private String content;
    private String description;
    private LocalDateTime date;

}
