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

    int count();

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

    @Query("SELECT DISTINCT r FROM Restaurant r " +
            "LEFT JOIN r.cuisines c " +
            "LEFT JOIN r.meals m " +
            "LEFT JOIN r.purposes p " +
            "WHERE (:cuisineNames IS NULL OR c.name IN :cuisineNames) " +
            "AND (:cityList IS NULL OR r.city IN :cityList)" +
            "AND (:mealList IS NULL OR m.name IN :mealList)" +
            "AND (:minRating IS NULL OR r.rating >= :minRating)" +
            "AND (:purposeList IS NULL OR p.name IN :purposeList)")
    List<Restaurant> findRestaurantsByFilters(@Param("cuisineNames") List<String> cuisineNames,
                                              @Param("cityList") List<String> cityList,
                                              @Param("mealList") List<String> mealList,
                                              @Param("minRating") int minRating,
                                              @Param("purposeList") List<String> purposeList);

}
