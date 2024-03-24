package etu.kefengroup.turistomer.rest;

import etu.kefengroup.turistomer.entity.RecommendationEntity;
import etu.kefengroup.turistomer.dto.Prediction;
import etu.kefengroup.turistomer.dto.Prompt;
import etu.kefengroup.turistomer.service.ModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("${model.api.path}")
public class ModelRestController {

    private ModelService modelService;

    @Autowired
    public ModelRestController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping
    public List<? extends RecommendationEntity> getPrompt(@RequestBody Prompt prompt){
        log.info("Got prompt: " + prompt);
        Prediction prediction = modelService.sendPromptToModel(prompt);
        if(prompt.getType().equals("restaurant")){
            return modelService.getRestaurantRecommendations(prediction, prompt.getCoordinates());
        }else if (prompt.getType().equals("hotel")){
            return modelService.getHotelRecommendations(prediction, prompt.getCoordinates());
        }

        return null;
    }
}
