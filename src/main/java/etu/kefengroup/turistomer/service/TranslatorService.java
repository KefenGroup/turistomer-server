package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dto.TranslatorMessage;
import etu.kefengroup.turistomer.dto.TranslatorRequest;
import etu.kefengroup.turistomer.dto.TranslatorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TranslatorService {
    @Qualifier("openAIConfig")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.chatgpt.model}")
    private String model;

    @Value("${openai.chatgpt.max-completions}")
    private int maxCompletions;

    @Value("${openai.chatgpt.temperature}")
    private double temperature;

    @Value("${openai.chatgpt.max_tokens}")
    private int maxTokens;

    @Value("${openai.chatgpt.api.url}")
    private String apiUrl;

    public TranslatorResponse translatePrompt(String prompt) {
        String processedPrompt = "Translate the following text into English unless it is in English: " + prompt;
        TranslatorRequest request = new TranslatorRequest(model,
                List.of(new TranslatorMessage("user", processedPrompt)),
                maxCompletions,
                temperature,
                maxTokens);

        TranslatorResponse response = restTemplate.postForObject(apiUrl, request, TranslatorResponse.class);
        return response;
    }
}
