package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.entity.Hotel;
import etu.kefengroup.turistomer.entity.model.Coordinates;
import etu.kefengroup.turistomer.entity.model.Prediction;

import java.util.List;

public interface HotelService {

    List<Hotel> findAll();

    List<Hotel> findByPage(int no, int size);

    Hotel findById(int id);

    Hotel save(Hotel hotel);

    void deleteById(int id);

    int getCount();

    List<Hotel> findByPrediction(Prediction prediction, Coordinates coordinates);

}
