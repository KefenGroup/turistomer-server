package etu.kefengroup.turistomer.dto;

import etu.kefengroup.turistomer.entity.RecommendationEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class RecommendationDTO {
    Filter filter;
    List<? extends RecommendationEntity> recommendations;
}
