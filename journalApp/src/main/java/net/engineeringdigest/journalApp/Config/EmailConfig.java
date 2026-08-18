package net.engineeringdigest.journalApp.Config;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailConfig {

    private String sendTo;
    private String subject;
    private String body;
}
