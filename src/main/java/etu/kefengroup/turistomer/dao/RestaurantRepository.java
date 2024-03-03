package etu.kefengroup.turistomer.dao;

import etu.kefengroup.turistomer.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
}
