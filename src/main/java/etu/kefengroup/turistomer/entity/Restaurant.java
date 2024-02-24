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
    @Column(name="restaurant_id")
    private int id;

    @Column(name="name")
    @NonNull
    private String name;

    @Column(name="city")
    @NonNull
    private String city;

    @Column(name="link")
    @NonNull
    private String link;

    @Column(name="rating")
    private int rating = -1;        //default value for rating is -1

    @Column(name="price_lower")
    private int priceLower = -1;    //default value for price is -1

    @Column(name="price_higher")
    private int priceHigher = -1;

    @Column(name="longitude")
    private float longitude;

    @Column(name="latitude")
    private float latitude;

    @Column(name="address")
    private String address;

    //TODO define suitable constructors
}
