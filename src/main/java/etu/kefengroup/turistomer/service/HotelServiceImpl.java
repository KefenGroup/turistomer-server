package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dao.HotelRepository;
import etu.kefengroup.turistomer.entity.Hotel;
import etu.kefengroup.turistomer.rest.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelServiceImpl implements HotelService{

    private HotelRepository hotelRepository;

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    @Override
    public Hotel findById(int id) {
        Optional<Hotel> result = hotelRepository.findById(id);
        Hotel hotel = null;

        if(result.isPresent()){
            hotel = result.get();
        }else {
            throw new EntityNotFoundException("Hotel id not found - " + id);
        }

        return hotel;
    }

    @Override
    public Hotel save(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    @Override
    public void deleteById(int id) {
        hotelRepository.deleteById(id);
    }
}
