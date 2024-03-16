package etu.kefengroup.turistomer.rest;

import etu.kefengroup.turistomer.entity.RecommendationEntity;
import etu.kefengroup.turistomer.entity.model.Prediction;
import etu.kefengroup.turistomer.entity.model.Prompt;
import etu.kefengroup.turistomer.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("${model.api.path}")
public class ModelRestController {

    private ModelService modelService;

    @Autowired
    public ModelRestController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping
    public List<? extends RecommendationEntity> getPrompt(@RequestBody Prompt prompt){
        Prediction prediction = modelService.sendPromptToModel(prompt);
        if(prompt.getType().equals("restaurant")){
            return modelService.getRestaurantRecommendations(prediction, prompt.getCoordinates());
        }else if (prompt.getType().equals("hotel")){
            return null;
        }

        return null;
    }
}
