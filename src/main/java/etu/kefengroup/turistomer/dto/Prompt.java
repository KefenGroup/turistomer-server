package etu.kefengroup.turistomer.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Prompt {
    private String prompt;
    private String type;
    private Coordinates coordinates;

}
