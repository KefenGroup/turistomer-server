package etu.kefengroup.turistomer.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;
}
