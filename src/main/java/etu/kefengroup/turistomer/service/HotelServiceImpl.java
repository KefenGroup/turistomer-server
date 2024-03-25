package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dao.HotelRepository;
import etu.kefengroup.turistomer.dto.RecommendationDTO;
import etu.kefengroup.turistomer.entity.Hotel;
import etu.kefengroup.turistomer.dto.Coordinates;
import etu.kefengroup.turistomer.dto.Prediction;
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
    private Prediction chainedPrediction;

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
        chainedPrediction = new Prediction();
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
    public RecommendationDTO findByPrediction(Prediction prediction, Coordinates coordinates) {
        updateChainedPrediction(prediction);
        hotelRecommendations = new ArrayList<>();

        if(prediction.getAmenity() != null){
            hotelRecommendations.addAll(findByPredictionAmenityHelper(prediction.getAmenity()));
        }

        if(prediction.getIsClose() != null && !prediction.getIsClose().contains(1)){
            findByPredictionLocationHelper(prediction.getLocation());
        }

        if((prediction.getCuisine() == null && prediction.getLocation() == null)
                || (prediction.getIsClose() != null && prediction.getIsClose().contains(1))){
            findByPredictionCloseHelper(coordinates);
        }

        if(prediction.getIsCheap() != null && prediction.getIsCheap().contains(1)
                && (prediction.getIsExpensive() != null && prediction.getIsExpensive().contains(1))){
            findByPredictionCheapHelper(3000);
        }

        if(prediction.getIsExpensive() != null && prediction.getIsExpensive().contains(1)){
            findByPredictionExpensiveHelper(5000);
        }

        return new RecommendationDTO(chainedPrediction, hotelRecommendations);
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
