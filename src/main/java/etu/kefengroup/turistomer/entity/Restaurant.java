package etu.kefengroup.turistomer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

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

    @Column(name="price_type")
    private String priceType;

    @Column(name="longitude")
    private float longitude = -1.0f;

    @Column(name="latitude")
    private float latitude = -1.0f;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinTable(name = "Restaurant_Cuisine", joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "cuisine_id"))
    private Set<Cuisine> cuisines;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinTable(name = "Restaurant_Meal", joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_id"))
    private Set<Meal> meals;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinTable(name = "Restaurant_Purpose", joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "purpose_id"))
    private Set<Purpose> purposes;


}
