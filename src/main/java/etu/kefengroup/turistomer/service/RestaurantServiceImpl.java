package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dao.RestaurantRepository;
import etu.kefengroup.turistomer.dto.RecommendationDTO;
import etu.kefengroup.turistomer.entity.Restaurant;
import etu.kefengroup.turistomer.dto.Coordinates;
import etu.kefengroup.turistomer.dto.Prediction;
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
    private Prediction chainedPrediction;
    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
        chainedPrediction = new Prediction();
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
    public RecommendationDTO findByPrediction(Prediction prediction, Coordinates coordinates) {
        updateChainedPrediction(prediction);
        restaurantRecommendations = new ArrayList<>();

        if(chainedPrediction.getCuisine() != null){
            restaurantRecommendations.addAll(findByPredictionCuisineHelper(chainedPrediction.getCuisine()));
        }

        if(chainedPrediction.getLocation() != null && chainedPrediction.getIsClose() != null && !chainedPrediction.getIsClose().contains(1)){
            findByPredictionLocationHelper(chainedPrediction.getLocation());
        }

        if((chainedPrediction.getCuisine() == null && chainedPrediction.getLocation() == null)
                || (chainedPrediction.getIsClose() != null && chainedPrediction.getIsClose().contains(1))){
            findByPredictionCloseHelper(coordinates);
        }

        if(chainedPrediction.getMeal() != null && chainedPrediction.getMeal().contains("breakfast")){
            findByPredictionMealHelper(chainedPrediction.getMeal());
        }

        if(chainedPrediction.getAmenity() != null){
            findByPredictionPurposeHelper(chainedPrediction.getAmenity());
        }

        if(chainedPrediction.getIsCheap() != null && chainedPrediction.getIsCheap().contains(1)
                && (chainedPrediction.getIsExpensive() != null && chainedPrediction.getIsExpensive().contains(1))){
            findByPredictionCheapHelper(300);
        }

        if(chainedPrediction.getIsExpensive() != null && chainedPrediction.getIsExpensive().contains(1)){
            findByPredictionExpensiveHelper(1200);
        }

        return new RecommendationDTO(chainedPrediction, restaurantRecommendations);
    }

    @Override
    public void resetPrediction() {
        chainedPrediction = new Prediction();
    }

    private void updateChainedPrediction(Prediction newPrediction){
        if (newPrediction.getCuisine() != null && !newPrediction.getCuisine().equals(chainedPrediction.getCuisine())) {
            chainedPrediction.setCuisine(newPrediction.getCuisine());
        }
        if (newPrediction.getLocation() != null && !newPrediction.getLocation().equals(chainedPrediction.getLocation())) {
            chainedPrediction.setLocation(newPrediction.getLocation());
        }
        if (newPrediction.getMeal() != null && !newPrediction.getMeal().equals(chainedPrediction.getMeal())) {
            chainedPrediction.setMeal(newPrediction.getMeal());
        }
        if (newPrediction.getIsClose() != null && !newPrediction.getIsClose().equals(chainedPrediction.getIsClose())) {
            chainedPrediction.setIsClose(newPrediction.getIsClose());
        }
        if (newPrediction.getPrice() != null && !newPrediction.getPrice().equals(chainedPrediction.getPrice())) {
            chainedPrediction.setPrice(newPrediction.getPrice());
        }
        if (newPrediction.getIsCheap() != null && !newPrediction.getIsCheap().equals(chainedPrediction.getIsCheap())) {
            chainedPrediction.setIsCheap(newPrediction.getIsCheap());
        }
        if (newPrediction.getIsExpensive() != null && !newPrediction.getIsExpensive().equals(chainedPrediction.getIsExpensive())) {
            chainedPrediction.setIsExpensive(newPrediction.getIsExpensive());
        }
        if (newPrediction.getAmenity() != null && !newPrediction.getAmenity().equals(chainedPrediction.getAmenity())) {
            chainedPrediction.setAmenity(newPrediction.getAmenity());
        }
        if (newPrediction.getRating() != null && !newPrediction.getRating().equals(chainedPrediction.getRating())) {
            chainedPrediction.setRating(newPrediction.getRating());
        }
    }

    private List<Restaurant> findByPredictionCuisineHelper(List<String> cuisines){
        List<String> turkishList = new ArrayList<>();
        for(String c : cuisines){
            if(EnglishToTurkishMappings.englishToTurkishCuisineMap.get(c) != null)
                turkishList.add(EnglishToTurkishMappings.englishToTurkishCuisineMap.get(c));
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
        GeoLocation.GeoLocationRange range = GeoLocation.getRange(coordinates.getLatitude(),coordinates.getLongitude(),5);

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
                        || !restaurant.getPriceType().equals("expensive")))
                .collect(Collectors.toList());
    }

    private void findByPredictionExpensiveHelper(int lowerThreshold) {
        restaurantRecommendations = restaurantRecommendations.stream()
                .filter(restaurant ->
                        (restaurant.getPriceLower() != -1 && restaurant.getPriceLower() > lowerThreshold)
                        || !restaurant.getPriceType().equals("cheap"))
                .collect(Collectors.toList());
    }
}
