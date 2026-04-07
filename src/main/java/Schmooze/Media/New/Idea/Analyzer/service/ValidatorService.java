package Schmooze.Media.New.Idea.Analyzer.service;

import Schmooze.Media.New.Idea.Analyzer.model.IdeaReport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ValidatorService {



    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public IdeaReport validateIdea(String userIdea) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String prompt = "Return ONLY valid JSON. No explanation, no markdown.\n"
                    + "{\n"
                    + "  \"problemSummary\": \"\",\n"
                    + "  \"targetCustomer\": \"\",\n"
                    + "  \"marketAnalysis\": \"\",\n"
                    + "  \"competitors\": \"\",\n"
                    + "  \"techStack\": \"\",\n"
                    + "  \"riskLevel\": \"Low/Medium/High\",\n"
                    + "  \"profitabilityScore\": number\n"
                    + "}\n"
                    + "Idea: " + userIdea;

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);

            Map<String, Object> part = new HashMap<>();
            part.put("parts", List.of(textPart));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(part));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(apiUrl + apiKey, request, String.class);

            JsonNode root = objectMapper.readTree(response);

            String aiText = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            // 🔥 Clean response
            String cleaned = aiText
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            int start = cleaned.indexOf("{");
            int end = cleaned.lastIndexOf("}");

            if (start != -1 && end != -1) {
                cleaned = cleaned.substring(start, end + 1);
            }

            JsonNode aiJson = objectMapper.readTree(cleaned);

            IdeaReport report = new IdeaReport();
            report.setOriginalIdea(userIdea);
            report.setProblemSummary(aiJson.path("problemSummary").asText());
            report.setTargetCustomer(aiJson.path("targetCustomer").asText());
            report.setMarketAnalysis(aiJson.path("marketAnalysis").asText());
            report.setCompetitors(aiJson.path("competitors").asText());
            report.setTechStack(aiJson.path("techStack").asText());
            report.setRiskLevel(aiJson.path("riskLevel").asText());
            report.setProfitabilityScore(aiJson.path("profitabilityScore").asInt());

            return report; // ✅ NO DATABASE

        } catch (Exception e) {
            e.printStackTrace();

            IdeaReport fallback = new IdeaReport();
            fallback.setOriginalIdea(userIdea);
            fallback.setProblemSummary("AI processing failed");
            fallback.setRiskLevel("Unknown");
            fallback.setProfitabilityScore(0);

            return fallback;
        }
    }


}