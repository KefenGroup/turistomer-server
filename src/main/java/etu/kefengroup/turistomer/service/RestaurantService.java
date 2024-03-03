package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.entity.Restaurant;

import java.util.List;

public interface RestaurantService {

    List<Restaurant> findAll();

    Restaurant findById(int id);

    Restaurant save(Restaurant restaurant);

    void deleteById(int id);
}
