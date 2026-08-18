package net.engineeringdigest.journalApp.Config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "WeatherConfig")
@Data
@NoArgsConstructor
public class WeatherConfig {

    @Id
    private String id;

    private String key;
    private String value;
}