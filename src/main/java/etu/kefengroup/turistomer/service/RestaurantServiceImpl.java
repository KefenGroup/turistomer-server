package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dao.RestaurantRepository;
import etu.kefengroup.turistomer.entity.Restaurant;
import etu.kefengroup.turistomer.entity.model.Prediction;
import etu.kefengroup.turistomer.utils.EnglishToTurkishMappings;
import etu.kefengroup.turistomer.rest.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService{

    private RestaurantRepository restaurantRepository;
    private List<Restaurant> restaurantRecommendations;

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

    @Override
    public List<Restaurant> findByPrediction(Prediction prediction) {
        restaurantRecommendations = new ArrayList<>();

        if(prediction.getCuisine() != null){
            restaurantRecommendations.addAll(findByPredictionCuisineHelper(prediction.getCuisine()));
        }

        if(prediction.getIsClose() != null && !prediction.getIsClose().contains(1)){
            findByPredictionLocationHelper(prediction.getLocation());
        }

        //TODO eğer location veya cuisine verilmediyse yakındakiler önerilere eklenecek

        return restaurantRecommendations;
    }

    private List<Restaurant> findByPredictionCuisineHelper(List<String> cuisines){
        List<String> turkishList = new ArrayList<>();
        for(String c : cuisines){
            if(EnglishToTurkishMappings.englishToTurkishMap.get(c) != null)
                turkishList.add(EnglishToTurkishMappings.englishToTurkishMap.get(c));
        }

        return restaurantRepository.findRestaurantsByCuisineNames(turkishList, cuisines.get(0));
    }

    private void findByPredictionLocationHelper(List<String> locations) {
        if(restaurantRecommendations.isEmpty()){
            restaurantRecommendations.addAll(restaurantRepository.findRestaurantsByCityList(locations));
        }
        else{
            restaurantRecommendations =restaurantRecommendations.stream()
                    .filter(restaurant -> locations.stream()
                            .anyMatch(city -> restaurant.getCity().equals(city)))
                    .collect(Collectors.toList());
        }
    }
}
