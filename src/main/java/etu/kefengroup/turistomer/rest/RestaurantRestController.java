package etu.kefengroup.turistomer.rest;

import etu.kefengroup.turistomer.entity.Restaurant;
import etu.kefengroup.turistomer.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class RestaurantRestController {

    private RestaurantService restaurantService;

    @Autowired
    public RestaurantRestController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/restaurants/{no}/{size}")
    public List<Restaurant> getRestaurantList(@PathVariable int no,
                                      @PathVariable int size) {
        return restaurantService.findByPage(no, size);
    }

    @GetMapping("/restaurants")
    public List<Restaurant> findAll(){
        return restaurantService.findAll();
    }

    @GetMapping("/restaurants/{restaurantId}")
    public Restaurant getRestaurant(@PathVariable int restaurantId){
        Restaurant restaurant = restaurantService.findById(restaurantId);

        if(restaurant == null){
            throw new EntityNotFoundException("Restaurant id not found - " + restaurantId);
        }

        return restaurant;
    }

    @PostMapping("/restaurants")
    public Restaurant addRestaurant(@RequestBody Restaurant restaurant){
        restaurant.setId(0);
        Restaurant dbRestaurant = restaurantService.save(restaurant);

        return dbRestaurant;
    }

    @PutMapping("/restaurants")
    public Restaurant updateRestaurant(@RequestBody Restaurant restaurant){
        Restaurant dbRestaurant = restaurantService.save(restaurant);

        return dbRestaurant;
    }

    @DeleteMapping("/restaurants/{restaurantId}")
    public String deleteRestaurant(@PathVariable int restaurantId){
        Restaurant restaurant = restaurantService.findById(restaurantId);

        if(restaurant == null){
            throw new EntityNotFoundException("Restaurant id not found - " + restaurantId);
        }

        restaurantService.deleteById(restaurantId);
        return "Deleted restaurant id - " + restaurantId;
    }

}
