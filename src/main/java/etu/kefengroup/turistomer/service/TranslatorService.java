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

    // TODO handle preprompt to work as expected
    private final String prepromptForTranslation = "Please ignore all previous instructions. " +
            "Please respond only in English. Do not explain what you are doing. Do not self reference. You are an expert translator. "+
            "Do not change the text if it is in English" +
            "Translate the following text to English:\n";

    public TranslatorResponse translatePrompt(String prompt) {
        String processedPrompt = prepromptForTranslation + prompt;
        TranslatorRequest request = new TranslatorRequest(model,
                List.of(new TranslatorMessage("user", processedPrompt)),
                maxCompletions,
                temperature,
                maxTokens);

        TranslatorResponse response = restTemplate.postForObject(apiUrl, request, TranslatorResponse.class);
        return response;
    }
}
