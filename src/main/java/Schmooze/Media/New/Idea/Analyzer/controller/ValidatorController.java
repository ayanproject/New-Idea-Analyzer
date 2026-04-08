package Schmooze.Media.New.Idea.Analyzer.controller;

import Schmooze.Media.New.Idea.Analyzer.model.IdeaReport;
import Schmooze.Media.New.Idea.Analyzer.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
    "https://effulgent-mochi-...netlify.app"
}) // Allow frontend to access
public class ValidatorController {

    @Autowired
    private ValidatorService service;

    @PostMapping("/validate")
    public IdeaReport validate(@RequestBody Map<String, String> payload) {
        String idea = payload.get("idea");
        return service.validateIdea(idea);
    }


}
