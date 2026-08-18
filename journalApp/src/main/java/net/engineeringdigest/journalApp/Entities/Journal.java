package net.engineeringdigest.journalApp.Entities;

import lombok.Data;
import net.engineeringdigest.journalApp.Config.SentimentEnumConfig;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private SentimentEnumConfig sentiment;

}
