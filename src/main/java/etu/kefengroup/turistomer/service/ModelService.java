package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dto.TranslatorResponse;
import etu.kefengroup.turistomer.entity.RecommendationEntity;
import etu.kefengroup.turistomer.dto.Coordinates;
import etu.kefengroup.turistomer.dto.Prediction;
import etu.kefengroup.turistomer.dto.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.util.List;


@Service
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

    public Prediction sendPromptToModel(Prompt prompt){
        TranslatorResponse translatedPrompt = translatorService.translatePrompt(prompt.getPrompt());
        String englishPromptAsString = translatedPrompt.getChoices().get(0).getMessage().getContent();
        prompt.setPrompt(englishPromptAsString);

        HttpEntity<Prompt> requestEntity = new HttpEntity<>(prompt);
        ResponseEntity<Prediction> response = restTemplate.exchange(
                modelApiUrl,
                HttpMethod.POST,
                requestEntity,
                Prediction.class
        );

        return response.getBody();
    }

    public List<? extends RecommendationEntity> getRestaurantRecommendations(Prediction prediction, Coordinates coordinates){
        return restaurantService.findByPrediction(prediction, coordinates);
    }

    public List<? extends RecommendationEntity> getHotelRecommendations(Prediction prediction, Coordinates coordinates){
        return hotelService.findByPrediction(prediction, coordinates);
    }
}
