package etu.kefengroup.turistomer.entity.model;

import lombok.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class Prediction {
    private String type;
    private List<String> entity;
    private List<Integer> position;
    private List<Float> probability;
}