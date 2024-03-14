package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.entity.model.Prediction;
import etu.kefengroup.turistomer.entity.model.Prompt;
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

    @Autowired
    public ModelService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Prediction sendPromptToModel(Prompt prompt){
        HttpEntity<Prompt> requestEntity = new HttpEntity<>(prompt);
        ResponseEntity<Prediction> response = restTemplate.exchange(
                modelApiUrl,
                HttpMethod.POST,
                requestEntity,
                Prediction.class
        );

        return response.getBody();
    }
}
