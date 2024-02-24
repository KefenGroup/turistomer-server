package etu.kefengroup.turistomer.dao;

import etu.kefengroup.turistomer.entity.Hotel;

public interface HotelDAO {
    void save(Hotel hotel);

    Hotel findById(Integer id);

    void update(Hotel hotel);

    void delete(Integer id);
}
