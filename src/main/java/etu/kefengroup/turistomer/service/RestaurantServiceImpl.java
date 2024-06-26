package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dao.RestaurantRepository;
import etu.kefengroup.turistomer.dto.RecommendationDTO;
import etu.kefengroup.turistomer.entity.Restaurant;
import etu.kefengroup.turistomer.dto.Coordinates;
import etu.kefengroup.turistomer.dto.Filter;
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
    private Filter chainedFilter;
    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
        chainedFilter = new Filter();
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
    public RecommendationDTO findByPrediction(Filter filter, Coordinates coordinates) {
        updateChainedPrediction(filter);
        restaurantRecommendations = new ArrayList<>();

        if(chainedFilter.getCuisine() != null){
            restaurantRecommendations.addAll(findByPredictionCuisineHelper(chainedFilter.getCuisine()));
        }

        if(chainedFilter.getLocation() != null && chainedFilter.getIsClose() != null && !chainedFilter.getIsClose().contains(1)){
            restaurantRecommendations = findByPredictionLocationHelper(chainedFilter.getLocation());
        }

        if((chainedFilter.getCuisine() == null || chainedFilter.getCuisine().isEmpty()) &&
                (chainedFilter.getLocation() == null || chainedFilter.getLocation().isEmpty())){
            restaurantRecommendations = findByPredictionCloseHelper(coordinates);
        }

        if(chainedFilter.getIsClose() != null && chainedFilter.getIsClose().contains(1)){
            restaurantRecommendations = findByPredictionCloseHelper(restaurantRecommendations, coordinates);
        }

        if(chainedFilter.getMeal() != null){
            restaurantRecommendations = findByPredictionMealHelper(chainedFilter.getMeal());
        }

        if(chainedFilter.getAmenity() != null){
            restaurantRecommendations = findByPredictionPurposeHelper(chainedFilter.getAmenity());
        }

        if(chainedFilter.getIsCheap() != null && chainedFilter.getIsCheap().contains(1)){
            restaurantRecommendations = findByPredictionCheapHelper(restaurantRecommendations ,300);
        }

        if(chainedFilter.getIsExpensive() != null && chainedFilter.getIsExpensive().contains(1)){
            restaurantRecommendations = findByPredictionExpensiveHelper(restaurantRecommendations, 1200);
        }

        return new RecommendationDTO(chainedFilter, restaurantRecommendations);
    }

    @Override
    public List<Restaurant> findRestaurantsByFilters(Filter filter) {
        List<Restaurant> filteredRestaurants = restaurantRepository.findRestaurantsByFilters(filter.getCuisine(), filter.getLocation(),
                filter.getMeal(), filter.getMinRating(), filter.getAmenity());

        if(filter.getIsClose().contains(1)){
            filteredRestaurants = findByPredictionCloseHelper(filteredRestaurants, filter.getCoordinates());
        }

        if(filter.getIsCheap().contains(1)){
            filteredRestaurants = findByPredictionCheapHelper(filteredRestaurants, 300);
        }

        if(filter.getIsExpensive().contains(1)){
            filteredRestaurants = findByPredictionExpensiveHelper(filteredRestaurants, 1200);
        }

        return filteredRestaurants;
    }

    @Override
    public void resetPrediction() {
        chainedFilter = new Filter();
    }

    private void updateChainedPrediction(Filter newFilter){
        if (newFilter.getCuisine() != null && !newFilter.getCuisine().equals(chainedFilter.getCuisine())) {
            chainedFilter.setCuisine(newFilter.getCuisine());
        }
        if (newFilter.getLocation() != null && !newFilter.getLocation().equals(chainedFilter.getLocation())) {
            chainedFilter.setLocation(newFilter.getLocation());
        }
        if (newFilter.getMeal() != null && !newFilter.getMeal().equals(chainedFilter.getMeal())) {
            chainedFilter.setMeal(newFilter.getMeal());
        }
        if (newFilter.getIsClose() != null && !newFilter.getIsClose().equals(chainedFilter.getIsClose())) {
            chainedFilter.setIsClose(newFilter.getIsClose());
        }
        if (newFilter.getPrice() != null && !newFilter.getPrice().equals(chainedFilter.getPrice())) {
            chainedFilter.setPrice(newFilter.getPrice());
        }
        if (newFilter.getIsCheap() != null && !newFilter.getIsCheap().equals(chainedFilter.getIsCheap())) {
            chainedFilter.setIsCheap(newFilter.getIsCheap());
        }
        if (newFilter.getIsExpensive() != null && !newFilter.getIsExpensive().equals(chainedFilter.getIsExpensive())) {
            chainedFilter.setIsExpensive(newFilter.getIsExpensive());
        }
        if (newFilter.getAmenity() != null && !newFilter.getAmenity().equals(chainedFilter.getAmenity())) {
            chainedFilter.setAmenity(newFilter.getAmenity());
        }
        if (newFilter.getRating() != null && !newFilter.getRating().equals(chainedFilter.getRating())) {
            chainedFilter.setRating(newFilter.getRating());
        }
    }

    private List<Restaurant> findByPredictionCuisineHelper(List<String> cuisines){
        return restaurantRepository.findRestaurantsByCuisineNames(cuisines, cuisines.get(0));
    }

    private List<Restaurant> findByPredictionLocationHelper(List<String> locations) {
        if(restaurantRecommendations.isEmpty()){
            return restaurantRepository.findRestaurantsByCityList(locations);
        }
        else{
            return restaurantRecommendations.stream()
                    .filter(restaurant -> locations.stream()
                            .anyMatch(city -> restaurant.getCity().equals(city)))
                    .collect(Collectors.toList());
        }
    }

    private List<Restaurant> findByPredictionMealHelper(List<String> meals) {
        return restaurantRecommendations.stream()
                .filter(restaurant -> meals.stream()
                        .anyMatch(meal -> restaurant.getMeals().stream()
                                .anyMatch(restaurantMeal -> restaurantMeal.getName().equalsIgnoreCase(meal))))
                .collect(Collectors.toList());
    }

    private List<Restaurant> findByPredictionPurposeHelper(List<String> purposes) {
        return restaurantRecommendations.stream()
                .filter(restaurant -> purposes.stream()
                        .anyMatch(purpose -> restaurant.getPurposes().stream()
                                .anyMatch(restaurantAmenity -> restaurantAmenity.getName().equalsIgnoreCase(purpose))))
                .collect(Collectors.toList());
    }

    private List<Restaurant> findByPredictionCloseHelper(List<Restaurant> list, Coordinates coordinates) {
        GeoLocation.GeoLocationRange range = GeoLocation.getRange(coordinates.getLatitude(),coordinates.getLongitude(),5);

        return list.stream()
                .filter(restaurant -> restaurant.getLatitude() >= range.getMinLatitude() && restaurant.getLatitude() <= range.getMaxLatitude() &&
                        restaurant.getLongitude() >= range.getMinLongitude() && restaurant.getLongitude() <= range.getMaxLongitude())
                .collect(Collectors.toList());

    }

    private List<Restaurant> findByPredictionCloseHelper(Coordinates coordinates) {
        GeoLocation.GeoLocationRange range = GeoLocation.getRange(coordinates.getLatitude(),coordinates.getLongitude(),5);

        return restaurantRepository.findRestaurantsByLocationRange(
                range.getMinLongitude(),range.getMaxLongitude(),
                range.getMinLatitude(), range.getMaxLatitude());

    }

    private List<Restaurant> findByPredictionCheapHelper(List<Restaurant> list, int higherThreshold) {
        List<Restaurant> filteredList = new ArrayList<>();
        for(Restaurant restaurant : list){
            if((restaurant.getPriceHigher() != -1 && restaurant.getPriceHigher() > higherThreshold) ||
                    !restaurant.getPriceType().contains("expensive")){
                filteredList.add(restaurant);
            }
        }
        return filteredList;
    }

    private List<Restaurant> findByPredictionExpensiveHelper(List<Restaurant> list, int lowerThreshold) {
        List<Restaurant> filteredList = new ArrayList<>();
        for(Restaurant restaurant : list){
            if((restaurant.getPriceLower() != -1 && restaurant.getPriceLower() > lowerThreshold) ||
                    !restaurant.getPriceType().contains("cheap")){
                filteredList.add(restaurant);
            }
        }
        return filteredList;
    }
}
