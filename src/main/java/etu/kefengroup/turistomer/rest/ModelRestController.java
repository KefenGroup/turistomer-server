package etu.kefengroup.turistomer.rest;

import etu.kefengroup.turistomer.dto.RecommendationDTO;
import etu.kefengroup.turistomer.dto.Filter;
import etu.kefengroup.turistomer.dto.Prompt;
import etu.kefengroup.turistomer.service.ModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public RecommendationDTO getPrompt(@RequestBody Prompt prompt){
        log.info("Got prompt: " + prompt);
        Filter filter = modelService.sendPromptToModel(prompt);
        if(prompt.getType().equals("restaurant")){
            return modelService.getRestaurantRecommendations(filter, prompt.getCoordinates());
        }else if (prompt.getType().equals("hotel")){
            return modelService.getHotelRecommendations(filter, prompt.getCoordinates());
        }

        return null;
    }

    @PostMapping("/reset")
    public void resetPredictionChain(){
        log.info("Prediction Chain deleted");
        modelService.resetPrediction();
    }
}
