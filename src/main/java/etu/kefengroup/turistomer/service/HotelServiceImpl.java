package etu.kefengroup.turistomer.service;

import etu.kefengroup.turistomer.dao.HotelRepository;
import etu.kefengroup.turistomer.entity.Hotel;
import etu.kefengroup.turistomer.entity.model.Coordinates;
import etu.kefengroup.turistomer.entity.model.Prediction;
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

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
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
    public List<Hotel> findByPrediction(Prediction prediction, Coordinates coordinates) {
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

        return hotelRecommendations;
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
        GeoLocation.GeoLocationRange range = GeoLocation.getRange(coordinates.getLatitude(),coordinates.getLongitude(),2);

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
