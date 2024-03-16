package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dao.RestaurantRepository;
import etu.kefengroup.turistomer.entity.Restaurant;
import etu.kefengroup.turistomer.entity.model.Coordinates;
import etu.kefengroup.turistomer.entity.model.Prediction;
import etu.kefengroup.turistomer.utils.EnglishToTurkishMappings;
import etu.kefengroup.turistomer.rest.EntityNotFoundException;
import etu.kefengroup.turistomer.utils.GeoLocation;
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
    public int getCount() {
        return restaurantRepository.count();
    }

    @Override
    public List<Restaurant> findByPrediction(Prediction prediction, Coordinates coordinates) {
        restaurantRecommendations = new ArrayList<>();

        if(prediction.getCuisine() != null){
            restaurantRecommendations.addAll(findByPredictionCuisineHelper(prediction.getCuisine()));
        }

        if(prediction.getIsClose() != null && !prediction.getIsClose().contains(1)){
            findByPredictionLocationHelper(prediction.getLocation());
        }

        if(prediction.getIsClose() != null && prediction.getIsClose().contains(1)){
            findByPredictionCloseHelper(coordinates);
        }

        if(prediction.getMeal() != null && prediction.getMeal().contains("breakfast")){
            findByPredictionMealHelper(prediction.getMeal());
        }

        if(prediction.getAmenity() != null){
            findByPredictionPurposeHelper(prediction.getAmenity());
        }

        if(prediction.getIsCheap() != null && prediction.getIsCheap().contains(1)
                && (prediction.getIsExpensive() != null && prediction.getIsExpensive().contains(1))){
            findByPredictionCheapHelper(300);
        }

        if(prediction.getIsExpensive() != null && prediction.getIsExpensive().contains(1)){
            findByPredictionExpensiveHelper(1200);
        }

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
            restaurantRecommendations = restaurantRecommendations.stream()
                    .filter(restaurant -> locations.stream()
                            .anyMatch(city -> restaurant.getCity().equals(city)))
                    .collect(Collectors.toList());
        }
    }

    private void findByPredictionCloseHelper(Coordinates coordinates) {
        GeoLocation.GeoLocationRange range = GeoLocation.getRange(coordinates.getLatitude(),coordinates.getLongitude(),2);

        if(restaurantRecommendations.isEmpty()){
            restaurantRecommendations.addAll(restaurantRepository.findRestaurantsByLocationRange(
                    range.getMinLongitude(),range.getMaxLongitude(),
                    range.getMinLatitude(), range.getMaxLatitude()
            ));
        }
        else{
            restaurantRecommendations =restaurantRecommendations.stream()
                    .filter(restaurant -> restaurant.getLatitude() >= range.getMinLatitude() && restaurant.getLatitude() <= range.getMaxLatitude() &&
                            restaurant.getLongitude() >= range.getMinLongitude() && restaurant.getLongitude() <= range.getMaxLongitude())
                    .collect(Collectors.toList());
        }
    }

    private void findByPredictionMealHelper(List<String> meals) {
        restaurantRecommendations = restaurantRecommendations.stream()
                .filter(restaurant -> restaurant.getMeals().stream()
                        .anyMatch(meal -> meal.getName().equalsIgnoreCase("breakfast") || meal.getName().equalsIgnoreCase("brunch")))
                .collect(Collectors.toList());
    }

    private void findByPredictionPurposeHelper(List<String> purposes) {
        restaurantRecommendations = restaurantRecommendations.stream()
                .filter(restaurant -> purposes.stream()
                        .anyMatch(purpose -> restaurant.getPurposes().stream()
                                .anyMatch(restaurantAmenity -> restaurantAmenity.getName().equalsIgnoreCase(purpose))))
                .collect(Collectors.toList());
    }

    private void findByPredictionCheapHelper(int higherThreshold) {
        restaurantRecommendations = restaurantRecommendations.stream()
                .filter(restaurant -> (
                        (restaurant.getPriceHigher() != -1 && restaurant.getPriceHigher() < higherThreshold)
                        || (restaurant.getPriceType().equals("cheap") || restaurant.getPriceType().equals("average"))))
                .collect(Collectors.toList());
    }

    private void findByPredictionExpensiveHelper(int lowerThreshold) {
        restaurantRecommendations = restaurantRecommendations.stream()
                .filter(restaurant ->
                        (restaurant.getPriceLower() != -1 && restaurant.getPriceLower() > lowerThreshold)
                        || (restaurant.getPriceType().equals("expensive") || restaurant.getPriceType().equals("average")))
                .collect(Collectors.toList());
    }
}
