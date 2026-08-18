package net.engineeringdigest.journalApp.POJOs;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
public class TextToAudio {

    private String textData;
}
