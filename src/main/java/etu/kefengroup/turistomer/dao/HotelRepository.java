package etu.kefengroup.turistomer.dao;

import etu.kefengroup.turistomer.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {
}
