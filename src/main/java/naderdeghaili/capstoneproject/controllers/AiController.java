package naderdeghaili.capstoneproject.controllers;

import naderdeghaili.capstoneproject.services.AiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, Object> payload) {
        List<Map<String, String>> messages =
                (List<Map<String, String>>) payload.get("messages");
        String reply = aiService.chat(messages);
        return Map.of("reply", reply);
    }
}