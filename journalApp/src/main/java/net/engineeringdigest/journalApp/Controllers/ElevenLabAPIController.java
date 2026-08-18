package net.engineeringdigest.journalApp.Controllers;

import net.engineeringdigest.journalApp.POJOs.TextToAudio;
import net.engineeringdigest.journalApp.Config.RestTemplateConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audio")
public class ElevenLabAPIController {

    @Autowired
    RestTemplateConfig restCaller;

    @Value("${elevenLabs.apiKey}")
    private String apiKey;

    @Value("${elevenLabs.voiceId}")
    private String voiceId;


    @PostMapping("/textToAudio")
    public ResponseEntity<byte[]> getTextToAudio(@RequestBody TextToAudio textData){

        String URl = "https://api.elevenlabs.io/v1/text-to-speech/" + voiceId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = """
            {
              "text": "%s",
              "model_id": "eleven_flash_v2_5"
            }
            """.formatted(textData.getTextData());

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restCaller.restTemplate().exchange(URl, HttpMethod.POST, entity, byte[].class);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.parseMediaType("audio/mpeg"));

        return new ResponseEntity<>(response.getBody(), responseHeaders, HttpStatus.OK);
    }
}