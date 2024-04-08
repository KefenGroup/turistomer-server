package etu.kefengroup.turistomer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorRequest {

    private String model;
    private List<TranslatorMessage> messages;
    private int n;
    private double temperature;
    private int max_tokens;
}