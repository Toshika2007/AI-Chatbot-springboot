package com.chatbot.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@CrossOrigin

public class ChatController {

    @Value("${groq.api.key}")
    private String apiKey;

    @PostMapping("/chat")

    public String chat(
            @RequestBody String userInput
    ) {

        try {

            HttpClient client =
                    HttpClient.newHttpClient();

            userInput =
                    userInput
                            .replace(
                                    "\"",
                                    "\\\""
                            );

            String json =
                    "{"
                           + "\"model\":\"openai/gpt-oss-20b\","
                            + "\"messages\":["
                            + "{"
                            + "\"role\":\"system\","
                            + "\"content\":\"Reply clearly in English. Give short answers.\""
                            + "},"
                            + "{"
                            + "\"role\":\"user\","
                            + "\"content\":\""
                            + userInput
                            + "\""
                            + "}"
                            + "]"
                            + "}";

            HttpRequest request =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            "https://api.groq.com/openai/v1/chat/completions"
                                    )
                            )
                            .header(
                                    "Authorization",
                                    "Bearer " + apiKey
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest
                                            .BodyPublishers
                                            .ofString(json)
                            )
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse
                                    .BodyHandlers
                                    .ofString()
                    );

            ObjectMapper mapper =
                    new ObjectMapper();

            JsonNode root =
                    mapper.readTree(
                            response.body());

            if(
                    root.has("choices")
                            &&
                            root.path("choices").size()>0
            ){

                return root
                        .path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();

            }

            return "API Error:\n\n"
                    +
                    response.body();

        }

        catch (Exception e) {

            return "Error: "
                    + e.getMessage();

        }

    }

}