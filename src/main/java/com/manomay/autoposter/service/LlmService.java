package com.manomay.autoposter.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;

import java.util.List;

@Service
public class LlmService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    private ChatLanguageModel geminiModel;
    private ChatLanguageModel gpt120bModel;
    private ChatLanguageModel nemotronModel;
    private ChatLanguageModel judgeModel;

    @PostConstruct
    public void init() {
        geminiModel = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName("gemini-2.5-flash-lite")
                .build();

        gpt120bModel = OpenAiChatModel.builder()
                .apiKey(openRouterApiKey)
                .baseUrl("https://openrouter.ai/api/v1")
                .modelName("openai/gpt-oss-120b:free")
                .build();

        nemotronModel = OpenAiChatModel.builder()
                .apiKey(openRouterApiKey)
                .baseUrl("https://openrouter.ai/api/v1")
                .modelName("nvidia/nemotron-3-super-120b-a12b:free")
                .build();

        judgeModel = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName("gemini-2.5-flash-lite")
                .build();
    }

    public String generatePost(String message, String streamLink, String platform) {
        String prompt = buildPrompt(message, streamLink, platform);

        // Generate from each model
        List<String> candidates = generateFromAllModels(prompt);

        // Judge picks the best
        return judge(candidates, platform);
    }

    private List<String> generateFromAllModels(String prompt) {
        String output1 = tryGenerate(geminiModel, prompt, "Gemini");
        String output2 = tryGenerate(gpt120bModel, prompt, "GPT-120B");
        String output3 = tryGenerate(nemotronModel, prompt, "Nemotron");

        return List.of(output1, output2, output3)
                .stream()
                .filter(s -> s != null && !s.isEmpty())
                .toList();
    }

    private String tryGenerate(ChatLanguageModel model, String prompt, String modelName) {
        try {
            ChatRequest request = ChatRequest.builder()
                    .messages(UserMessage.from(prompt))
                    .build();
            return model.chat(request).aiMessage().text();
        } catch (Exception e) {
            System.err.println(modelName + " failed: " + e.getMessage());
            return null;
        }
    }

    private String judge(List<String> candidates, String platform) {
        if (candidates.isEmpty()) return "Stream is live! Tune in now.";
        if (candidates.size() == 1) return candidates.get(0);

        StringBuilder judgePrompt = new StringBuilder();
        judgePrompt.append("You are a social media expert judging livestream announcements for ")
                   .append(platform)
                   .append(".\n");
        judgePrompt.append("Pick the BEST announcement based on: hype, clarity, engagement, brevity.\n");
        judgePrompt.append("Return ONLY the winning text, nothing else.\n\n");

        for (int i = 0; i < candidates.size(); i++) {
            judgePrompt.append("Option ").append(i + 1).append(":\n")
                       .append(candidates.get(i)).append("\n\n");
        }

        try {
            ChatRequest request = ChatRequest.builder()
                    .messages(UserMessage.from(judgePrompt.toString()))
                    .build();
            return judgeModel.chat(request).aiMessage().text();
        } catch (Exception e) {
            System.err.println("Judge failed, picking first candidate: " + e.getMessage());
            return candidates.get(0);
        }
    }

    private String buildPrompt(String message, String streamLink, String platform) {
        return switch (platform) {
            case "TELEGRAM" -> """
                You are a livestream announcer. Write ONE Telegram message.
                Rules:
                - Max 2 sentences
                - Casual and hype tone
                - End with the stream link on its own line
                - No hashtags
                - Do not explain yourself, just write the message
                Message: %s
                Link: %s
                """.formatted(message, streamLink);

            case "DISCORD" -> """
                You are a livestream announcer. Write ONE Discord announcement.
                Rules:
                - Start with @everyone
                - Max 3 sentences
                - Use **bold** for key words
                - Playful and energetic tone
                - End with the stream link on its own line
                - Do not give options, do not explain yourself, just write the announcement
                Message: %s
                Link: %s
                """.formatted(message, streamLink);
            default -> message + " " + streamLink;
        };
    }
}