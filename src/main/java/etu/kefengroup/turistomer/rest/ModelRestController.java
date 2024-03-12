package etu.kefengroup.turistomer.rest;

import etu.kefengroup.turistomer.entity.model.Prediction;
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
    public List<Prediction> getPrompt(@RequestBody String prompt){
        return modelService.sendPromptToModel(prompt);
    }
}
