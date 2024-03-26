package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dto.RecommendationDTO;
import etu.kefengroup.turistomer.entity.Restaurant;
import etu.kefengroup.turistomer.dto.Coordinates;
import etu.kefengroup.turistomer.dto.Filter;

import java.util.List;

public interface RestaurantService {

    List<Restaurant> findAll();

    List<Restaurant> findByPage(int no, int size);

    Restaurant findById(int id);

    Restaurant save(Restaurant restaurant);

    void deleteById(int id);

    int getCount();

    RecommendationDTO findByPrediction(Filter filter, Coordinates coordinates);

    List<Restaurant> findRestaurantsByFilters(Filter filter);
    void resetPrediction();
}
