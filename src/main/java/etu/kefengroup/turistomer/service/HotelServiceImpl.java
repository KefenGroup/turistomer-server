package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dao.HotelRepository;
import etu.kefengroup.turistomer.dto.RecommendationDTO;
import etu.kefengroup.turistomer.entity.Hotel;
import etu.kefengroup.turistomer.dto.Coordinates;
import etu.kefengroup.turistomer.dto.Filter;
import etu.kefengroup.turistomer.rest.EntityNotFoundException;
import etu.kefengroup.turistomer.utils.EnglishToTurkishMappings;
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

        if(filter.getAmenity() != null){
            hotelRecommendations.addAll(findByPredictionAmenityHelper(filter.getAmenity()));
        }

        if(filter.getIsClose() != null && !filter.getIsClose().contains(1)){
            findByPredictionLocationHelper(filter.getLocation());
        }

        if((filter.getCuisine() == null && filter.getLocation() == null)
                || (filter.getIsClose() != null && filter.getIsClose().contains(1))){
            findByPredictionCloseHelper(coordinates);
        }

        if(filter.getIsCheap() != null && filter.getIsCheap().contains(1)
                && (filter.getIsExpensive() != null && filter.getIsExpensive().contains(1))){
            findByPredictionCheapHelper(3000);
        }

        if(filter.getIsExpensive() != null && filter.getIsExpensive().contains(1)){
            findByPredictionExpensiveHelper(5000);
        }

        return new RecommendationDTO(chainedFilter, hotelRecommendations);
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
        List<String> turkishList = new ArrayList<>();
        for(String a : amenities){
            if(EnglishToTurkishMappings.englishToTurkishAmenityMap.get(a) != null)
                turkishList.add(EnglishToTurkishMappings.englishToTurkishAmenityMap.get(a));
        }

        return hotelRepository.findHotelByAmenityNames(turkishList);
    }

    private void findByPredictionLocationHelper(List<String> locations){
        if(hotelRecommendations.isEmpty()){
            hotelRecommendations.addAll(hotelRepository.findHotelsByCityList(locations));
        }
        else{
            hotelRecommendations = hotelRecommendations.stream()
                    .filter(hotel -> locations.stream()
                            .anyMatch(city -> hotel.getCity().equals(city)))
                    .collect(Collectors.toList());
        }
    }

    private void findByPredictionCloseHelper(Coordinates coordinates) {
        GeoLocation.GeoLocationRange range = GeoLocation.getRange(coordinates.getLatitude(),coordinates.getLongitude(),5);

        if(hotelRecommendations.isEmpty()){
            hotelRecommendations.addAll(hotelRepository.findHotelsByLocationRange(
                    range.getMinLongitude(),range.getMaxLongitude(),
                    range.getMinLatitude(), range.getMaxLatitude()
            ));
        }
        else{
            hotelRecommendations =hotelRecommendations.stream()
                    .filter(hotel -> hotel.getLatitude() >= range.getMinLatitude() && hotel.getLatitude() <= range.getMaxLatitude() &&
                            hotel.getLongitude() >= range.getMinLongitude() && hotel.getLongitude() <= range.getMaxLongitude())
                    .collect(Collectors.toList());
        }
    }

    private void findByPredictionCheapHelper(int higherThreshold) {
        hotelRecommendations = hotelRecommendations.stream()
                .filter(hotel -> (
                        (hotel.getPrice() != -1 && hotel.getPrice() < higherThreshold)))
                .collect(Collectors.toList());
    }

    private void findByPredictionExpensiveHelper(int lowerThreshold) {
        hotelRecommendations = hotelRecommendations.stream()
                .filter(hotel -> (
                        (hotel.getPrice() != -1 && hotel.getPrice() > lowerThreshold)))
                .collect(Collectors.toList());
    }
}
