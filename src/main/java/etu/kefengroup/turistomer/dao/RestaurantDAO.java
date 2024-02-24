package etu.kefengroup.turistomer.dao;

import etu.kefengroup.turistomer.entity.Restaurant;

public interface RestaurantDAO {
    void save(Restaurant restaurant);

    Restaurant findById(Integer id);

    void update(Restaurant restaurant);

    void delete(Integer id);

}
