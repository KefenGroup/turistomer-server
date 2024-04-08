package etu.kefengroup.turistomer.dao;

import etu.kefengroup.turistomer.entity.Hotel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends PagingAndSortingRepository<Hotel, Integer> {
    List<Hotel> findAll();

    Optional<Hotel> findById(int id);

    Hotel save(Hotel restaurant);

    void deleteById(int id);

    int count();

    @Query("SELECT DISTINCT h FROM Hotel h " +
            "JOIN h.amenities a " +
            "WHERE a.name IN :amenityNames ")
    List<Hotel> findHotelByAmenityNames(@Param("amenityNames") List<String> amenityNames);

    @Query("SELECT h FROM Hotel h WHERE h.city in :cityList")
    List<Hotel> findHotelsByCityList(@Param("cityList") List<String> cityList);

    @Query("SELECT h FROM Hotel h " +
            "WHERE h.longitude BETWEEN :minLongitude AND :maxLongitude " +
            "AND h.latitude BETWEEN :minLatitude AND :maxLatitude")
    List<Hotel> findHotelsByLocationRange(@Param("minLongitude") double minLongitude,
                                                    @Param("maxLongitude") double maxLongitude,
                                                    @Param("minLatitude") double minLatitude,
                                                    @Param("maxLatitude") double maxLatitude);

    @Query("SELECT DISTINCT h FROM Hotel h " +
            "LEFT JOIN h.amenities a " +
            "WHERE (:amenityList IS NULL OR a.name IN :amenityList) " +
            "AND (:cityList IS NULL OR h.city IN :cityList)" +
            "AND (:minRating IS NULL OR h.rating >= :minRating)")
    List<Hotel> findHotelsByFilters(@Param("amenityList") List<String> amenityList,
                                                  @Param("cityList") List<String> cityList,
                                                  @Param("minRating") int minRating);
}
