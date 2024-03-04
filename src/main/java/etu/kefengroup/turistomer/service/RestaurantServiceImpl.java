package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dao.RestaurantRepository;
import etu.kefengroup.turistomer.entity.Restaurant;
import etu.kefengroup.turistomer.rest.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService{

    private RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public List<Restaurant> findByPage(int no, int size) {
        Pageable pageable = PageRequest.of(no, size);
        Page<Restaurant> restaurantPage = restaurantRepository.findAll(pageable);
        return restaurantPage.toList();
    }


    @Override
    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    @Override
    public Restaurant findById(int id) {
        Optional<Restaurant> result = restaurantRepository.findById(id);
        Restaurant restaurant = null;

        if(result.isPresent()){
            restaurant = result.get();
        }else {
            throw new EntityNotFoundException("Restaurant id not found - " + id);
        }

        return restaurant;
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Override
    public void deleteById(int id) {
        restaurantRepository.deleteById(id);
    }
}
