package etu.kefengroup.turistomer.dao;

import etu.kefengroup.turistomer.entity.Restaurant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends PagingAndSortingRepository<Restaurant, Integer> {
    List<Restaurant> findAll();

    Optional<Restaurant> findById(int id);

    Restaurant save(Restaurant restaurant);

    void deleteById(int id);

    @Query("SELECT DISTINCT r FROM Restaurant r " +
            "JOIN r.cuisines c " +
            "WHERE c.name IN :cuisineNames " +
            "OR LOWER(r.name) LIKE LOWER(CONCAT('%', :partialCuisineName, '%'))")
    List<Restaurant> findRestaurantsByCuisineNames(@Param("cuisineNames") List<String> cuisineNames, @Param("partialCuisineName") String partialCuisineName);

    @Query("SELECT r FROM Restaurant r WHERE r.city in :cityList")
    List<Restaurant> findRestaurantsByCityList(@Param("cityList") List<String> cityList);

    @Query("SELECT r FROM Restaurant r " +
            "WHERE r.longitude BETWEEN :minLongitude AND :maxLongitude " +
            "AND r.latitude BETWEEN :minLatitude AND :maxLatitude")
    List<Restaurant> findRestaurantsByLocationRange(@Param("minLongitude") double minLongitude,
                                                    @Param("maxLongitude") double maxLongitude,
                                                    @Param("minLatitude") double minLatitude,
                                                    @Param("maxLatitude") double maxLatitude);


}
