package etu.kefengroup.turistomer.dao;

import etu.kefengroup.turistomer.entity.Hotel;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends PagingAndSortingRepository<Hotel, Integer> {
    List<Hotel> findAll();

    Optional<Hotel> findById(int id);

    Hotel save(Hotel restaurant);

    void deleteById(int id);

    int count();
}
