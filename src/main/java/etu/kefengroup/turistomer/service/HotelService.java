package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.entity.Hotel;

import java.util.List;

public interface HotelService {

    List<Hotel> findAll();

    Hotel findById(int id);

    Hotel save(Hotel hotel);

    void deleteById(int id);
}