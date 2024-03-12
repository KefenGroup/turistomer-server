package etu.kefengroup.turistomer.entity.model;

import lombok.*;

@Getter @Setter
public class Prompt {
    private String prompt;

    public Prompt(String prompt) {
        this.prompt = prompt;
    }
}
