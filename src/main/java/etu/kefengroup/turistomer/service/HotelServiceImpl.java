package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dao.HotelRepository;
import etu.kefengroup.turistomer.dto.RecommendationDTO;
import etu.kefengroup.turistomer.entity.Hotel;
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
public class HotelServiceImpl implements HotelService{

    private HotelRepository hotelRepository;
    private List<Hotel> hotelRecommendations;
    private Filter chainedFilter;

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
        chainedFilter = new Filter();
    }

    @Override
    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    @Override
    public List<Hotel> findByPage(int no, int size) {
        Pageable pageable = PageRequest.of(no, size);
        Page<Hotel> hotelPage = hotelRepository.findAll(pageable);
        return hotelPage.toList();
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

    @Override
    public int getCount() {
        return hotelRepository.count();
    }

    @Override
    public RecommendationDTO findByPrediction(Filter filter, Coordinates coordinates) {
        updateChainedPrediction(filter);
        hotelRecommendations = new ArrayList<>();

        if(chainedFilter.getAmenity() != null && !chainedFilter.getAmenity().isEmpty()){
            hotelRecommendations.addAll(findByPredictionAmenityHelper(filter.getAmenity()));
        }

        if(chainedFilter.getLocation() != null && !chainedFilter.getLocation().isEmpty()){
            hotelRecommendations = findByPredictionLocationHelper(chainedFilter.getLocation());
        }

        if((chainedFilter.getAmenity() == null || chainedFilter.getAmenity().isEmpty())
        && (chainedFilter.getLocation() == null || chainedFilter.getLocation().isEmpty())){
            hotelRecommendations = findByPredictionCloseHelper(coordinates);
        }

        if(chainedFilter.getIsClose() != null && chainedFilter.getIsClose().contains(1)){
            hotelRecommendations = findByPredictionCloseHelper(hotelRecommendations, coordinates);
        }

        if(chainedFilter.getIsCheap() != null && chainedFilter.getIsCheap().contains(1)){
            hotelRecommendations = findByPredictionCheapHelper(hotelRecommendations, 3000);
        }

        if(chainedFilter.getIsExpensive() != null && chainedFilter.getIsExpensive().contains(1)){
            hotelRecommendations = findByPredictionExpensiveHelper(hotelRecommendations, 5000);
        }

        return new RecommendationDTO(chainedFilter, hotelRecommendations);
    }

    @Override
    public List<Hotel> findHotelsByFilters(Filter filter) {
        List<Hotel> filteredHotels = hotelRepository.findHotelsByFilters(filter.getAmenity(),filter.getLocation(),filter.getMinRating());

        if(filter.getIsClose().contains(1)){
            filteredHotels = findByPredictionCloseHelper(filteredHotels, filter.getCoordinates());
        }

        if(filter.getIsCheap().contains(1)){
            filteredHotels = findByPredictionCheapHelper(filteredHotels, 3000);
        }

        if(filter.getIsExpensive().contains(1)){
            filteredHotels = findByPredictionExpensiveHelper(filteredHotels, 5000);
        }

        return filteredHotels;
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

    private List<Hotel> findByPredictionAmenityHelper(List<String> amenities){
        return hotelRepository.findHotelByAmenityNames(amenities);
    }

    private List<Hotel> findByPredictionLocationHelper(List<String> locations){
        if(hotelRecommendations.isEmpty()){
            return hotelRepository.findHotelsByCityList(locations);
        }
        else{
            return hotelRecommendations.stream()
                    .filter(hotel -> locations.stream()
                            .anyMatch(city -> hotel.getCity().equals(city)))
                    .collect(Collectors.toList());
        }
    }

    private List<Hotel> findByPredictionCloseHelper(List<Hotel> list, Coordinates coordinates) {
        GeoLocation.GeoLocationRange range = GeoLocation.getRange(coordinates.getLatitude(),coordinates.getLongitude(),5);

        return list.stream()
                .filter(hotel -> hotel.getLatitude() >= range.getMinLatitude() && hotel.getLatitude() <= range.getMaxLatitude() &&
                        hotel.getLongitude() >= range.getMinLongitude() && hotel.getLongitude() <= range.getMaxLongitude())
                .collect(Collectors.toList());
    }

    private List<Hotel> findByPredictionCloseHelper(Coordinates coordinates) {
        GeoLocation.GeoLocationRange range = GeoLocation.getRange(coordinates.getLatitude(),coordinates.getLongitude(),5);
        return hotelRepository.findHotelsByLocationRange(
                    range.getMinLongitude(),range.getMaxLongitude(),
                    range.getMinLatitude(), range.getMaxLatitude());
    }

    private List<Hotel> findByPredictionCheapHelper(List<Hotel> list, int higherThreshold) {
        return list.stream()
                .filter(hotel -> (
                        (hotel.getPrice() != -1 && hotel.getPrice() < higherThreshold)))
                .collect(Collectors.toList());
    }

    private List<Hotel> findByPredictionExpensiveHelper(List<Hotel> list, int lowerThreshold) {
        return list.stream()
                .filter(hotel -> (
                        (hotel.getPrice() != -1 && hotel.getPrice() > lowerThreshold)))
                .collect(Collectors.toList());
    }
}
