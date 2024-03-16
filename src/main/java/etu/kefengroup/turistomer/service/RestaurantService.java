package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.entity.Restaurant;
import etu.kefengroup.turistomer.entity.model.Coordinates;
import etu.kefengroup.turistomer.entity.model.Prediction;

import java.util.List;

public interface RestaurantService {

    List<Restaurant> findAll();

    List<Restaurant> findByPage(int no, int size);

    Restaurant findById(int id);

    Restaurant save(Restaurant restaurant);

    void deleteById(int id);

    int getCount();

    List<Restaurant> findByPrediction(Prediction prediction, Coordinates coordinates);
}
