package etu.kefengroup.turistomer.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @ToString
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="restaurant")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="city")
    @NonNull
    private String city;

    @Column(name="link")
    @NonNull
    private String link;

    @Column(name="name")
    @NonNull
    private String name;

    @Column(name="rating")
    private int rating = -1;        //default value for rating is -1

    @Column(name="price_lower")
    private int priceLower = -1;    //default value for price is -1

    @Column(name="price_higher")
    private int priceHigher = -1;

    //@Column(name="longitude")
    //private float longitude;

    //@Column(name="latitude")
    //private float latitude;

    //@Column(name="address")
    //private String address;

    public Restaurant(String city, String link, String name, int rating) {
        this(city, link, name);
        this.rating = rating;
    }

    public Restaurant(String city, String link, String name, int priceLower, int priceHigher) {
        this(city, link, name);
        this.priceLower = priceLower;
        this.priceHigher = priceHigher;
    }

    public Restaurant(String city, String link, String name, int rating, int priceLower, int priceHigher) {
        this(city, link, name, priceLower, priceHigher);
        this.rating = rating;
    }

//    public Restaurant(String city, String link, String name,
//                      int rating, int priceLower, int priceHigher,
//                      float longitude, float latitude, String address) {
//        this(city, link, name, rating, priceLower, priceHigher);
//        this.longitude = longitude;
//        this.latitude = latitude;
//        this.address = address;
//    }
}
