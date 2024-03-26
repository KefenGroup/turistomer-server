package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.util.regex.Pattern;


@Service
@Slf4j
public class ModelService {

    @Value("${model.api.url}")
    private String modelApiUrl;
    private final RestTemplate restTemplate;
    private final HotelService hotelService;
    private final RestaurantService restaurantService;

    private final TranslatorService translatorService;

    @Autowired
    public ModelService(RestTemplate restTemplate, HotelService hotelService, RestaurantService restaurantService, TranslatorService translatorService) {
        this.restTemplate = restTemplate;
        this.hotelService = hotelService;
        this.restaurantService = restaurantService;
        this.translatorService = translatorService;
    }

    public Filter sendPromptToModel(Prompt prompt){
//        TranslatorResponse translatedPrompt = translatorService.translatePrompt(prompt.getPrompt());
//        String englishPromptAsString = translatedPrompt.getChoices().get(0).getMessage().getContent();
//        String processedPrompt = removePunctuation(englishPromptAsString);
//        log.info("Translated Prompt: " + processedPrompt);
//
//        prompt.setPrompt(processedPrompt);

        HttpEntity<Prompt> requestEntity = new HttpEntity<>(prompt);
        ResponseEntity<Filter> response = restTemplate.exchange(
                modelApiUrl,
                HttpMethod.POST,
                requestEntity,
                Filter.class
        );

        log.info("Prediction response: " + response.getBody());
        return response.getBody();
    }

    public static String removePunctuation(String input) {
        Pattern pattern = Pattern.compile("\\p{Punct}");
        return input.replaceAll(pattern.toString(), "");
    }

    public RecommendationDTO getRestaurantRecommendations(Filter filter, Coordinates coordinates){
        return restaurantService.findByPrediction(filter, coordinates);
    }

    public RecommendationDTO getHotelRecommendations(Filter filter, Coordinates coordinates){
        return hotelService.findByPrediction(filter, coordinates);
    }

    public void resetPrediction(){
        restaurantService.resetPrediction();
        hotelService.resetPrediction();
    }
}
