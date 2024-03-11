package etu.kefengroup.turistomer.rest;

import etu.kefengroup.turistomer.entity.Hotel;
import etu.kefengroup.turistomer.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class HotelRestController {

    private HotelService hotelService;

    @Autowired
    public HotelRestController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/hotels/{no}/{size}")
    public List<Hotel> getHotelList(@PathVariable int no,
                                              @PathVariable int size) {
        return hotelService.findByPage(no, size);
    }

    @GetMapping("/hotels")
    public List<Hotel> findAll(){
        return hotelService.findAll();
    }

    @GetMapping("/hotels/{hotelId}")
    public Hotel getHotel(@PathVariable int hotelId){
        Hotel hotel = hotelService.findById(hotelId);

        if(hotel == null){
            throw new EntityNotFoundException("Hotel id not found - " + hotelId);
        }

        return hotel;
    }

    @PostMapping("/hotels")
    public Hotel addHotel(@RequestBody Hotel hotel){
        hotel.setId(0);
        Hotel dbHotel = hotelService.save(hotel);

        return dbHotel;
    }

    @PutMapping("/hotels")
    public Hotel updateHotel(@RequestBody Hotel hotel){
        Hotel dbHotel = hotelService.save(hotel);

        return dbHotel;
    }

    @DeleteMapping("/hotels/{hotelId}")
    public String deleteHotel(@PathVariable int hotelId){
        Hotel hotel = hotelService.findById(hotelId);

        if(hotel == null){
            throw new EntityNotFoundException("Hotel id not found - " + hotelId);
        }

        hotelService.deleteById(hotelId);
        return "Deleted hotel id - " + hotelId;
    }

}
