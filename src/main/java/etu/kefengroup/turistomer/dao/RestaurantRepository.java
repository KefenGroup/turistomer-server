package etu.kefengroup.turistomer.dao;

import etu.kefengroup.turistomer.entity.Restaurant;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends PagingAndSortingRepository<Restaurant, Integer> {
    List<Restaurant> findAll();

    Optional<Restaurant> findById(int id);

    Restaurant save(Restaurant restaurant);

    void deleteById(int id);
}
