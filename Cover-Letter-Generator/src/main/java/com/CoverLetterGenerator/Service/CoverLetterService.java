package com.CoverLetterGenerator.Service;

import Response.CoverLetterResponse;
import com.CoverLetterGenerator.Request.CoverLetterRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CoverLetterService {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/completions";
    private final OkHttpClient client = new OkHttpClient();
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    @Value("${openai.api.key}")
    private String openaiApiKey;

    public CoverLetterResponse generateCoverLetter(CoverLetterRequest request) {
        String prompt = String.format("Write a cover letter for %s, applying for %s at %s.",
                request.getName(), request.getJobTitle(), request.getCompanyName());

        String jsonRequestBody = String.format("{\"prompt\":\"%s\",\"max_tokens\":300,\"temperature\":0.7}", prompt);

        RequestBody body = RequestBody.create(jsonRequestBody.getBytes(), JSON);

        Request httpRequest = new Request.Builder()
                .url(OPENAI_API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + openaiApiKey)
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                String coverLetter = extractCoverLetter(responseBody);
                return new CoverLetterResponse(coverLetter);
            } else {
                throw new RuntimeException("Failed to generate cover letter: " + response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error calling OpenAI API", e);
        }
    }

    private String extractCoverLetter(String responseBody) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode choicesNode = rootNode.path("choices");
        if (choicesNode.isArray() && choicesNode.size() > 0) {
            return choicesNode.get(0).path("text").asText();
        }
        return "No cover letter generated.";

    }
}